package parfumerie.parfilya.services.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.models.neo4j.Brand;
import parfumerie.parfilya.models.neo4j.Category;
import parfumerie.parfilya.models.neo4j.ProductNode;
import parfumerie.parfilya.repositories.msql.ProductRepository;
import parfumerie.parfilya.repositories.neo4j.BrandRepository;
import parfumerie.parfilya.repositories.neo4j.CategoryRepository;
import parfumerie.parfilya.repositories.neo4j.ProductNodeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class Neo4jDataService {

    private static final Logger logger = LoggerFactory.getLogger(Neo4jDataService.class);

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductNodeRepository productNodeRepository;
    private final ProductRepository productRepository;

    public Neo4jDataService(CategoryRepository categoryRepository,
                            BrandRepository brandRepository,
                            ProductNodeRepository productNodeRepository,
                            ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productNodeRepository = productNodeRepository;
        this.productRepository = productRepository;
    }

    /**
     * Synchronise tous les produits MySQL vers Neo4j avec catégories
     */
    public Map<String, Object> syncAllProducts() {
        Map<String, Object> result = new HashMap<>();
        int created = 0;
        int linked = 0;

        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            SyncResult syncResult = syncProductToNeo4j(product);
            if (syncResult.created) created++;
            if (syncResult.linked) linked++;
        }

        result.put("totalProducts", products.size());
        result.put("created", created);
        result.put("linked", linked);
        result.put("message", "Synchronisation terminée");

        return result;
    }

    private static class SyncResult {
        boolean created;
        boolean linked;
        SyncResult(boolean created, boolean linked) {
            this.created = created;
            this.linked = linked;
        }
    }

    /**
     * Synchronise un produit MySQL vers Neo4j
     */
    public SyncResult syncProductToNeo4j(Product product) {
        boolean wasCreated = false;
        boolean wasLinked = false;

        // Vérifier si le ProductNode existe déjà
        Optional<ProductNode> existingNode = productNodeRepository.findByMysqlProductId(product.getId());
        ProductNode node;

        if (existingNode.isPresent()) {
            node = existingNode.get();
            logger.info("Produit {} existe déjà dans Neo4j", product.getName());
        } else {
            // Créer le ProductNode
            node = new ProductNode(
                    product.getId(),
                    product.getName(),
                    "SKU-" + product.getId()
            );
            node = productNodeRepository.save(node);
            wasCreated = true;
            logger.info("Produit {} créé dans Neo4j", product.getName());
        }

        // Vérifier si déjà lié à des catégories
        List<Category> existingCategories = categoryRepository.findCategoriesByProductId(product.getId());
        if (!existingCategories.isEmpty()) {
            logger.info("Produit {} déjà lié à {} catégorie(s)", product.getName(), existingCategories.size());
            return new SyncResult(wasCreated, false);
        }

        // Déterminer les catégories selon le nom/marque du produit
        String name = product.getName() != null ? product.getName().toLowerCase() : "";
        String brand = product.getBrand() != null ? product.getBrand().toLowerCase() : "";

        // Catégories Homme
        if (name.contains("homme") || name.contains("sauvage") || name.contains("bleu") ||
            brand.contains("boss") || brand.contains("armani") ||
            name.contains(" y ") || name.contains(" y\"") || brand.contains("ysl")) {
            linkToCategory(node, "Homme");
            wasLinked = true;
        }

        // Catégories Femme
        if (name.contains("femme") || name.contains("n°5") || name.contains("no.5") ||
            name.contains("bloom") || name.contains("la vie") || name.contains("belle") ||
            brand.contains("chanel") || brand.contains("gucci") || brand.contains("lancôme") || brand.contains("lancome")) {
            linkToCategory(node, "Femme");
            wasLinked = true;
        }

        // Luxe (marques premium)
        if (brand.contains("chanel") || brand.contains("dior") || brand.contains("guerlain") ||
            brand.contains("hermès") || brand.contains("hermes") || brand.contains("tom ford")) {
            linkToCategory(node, "Luxe");
            wasLinked = true;
        }

        // Si aucune catégorie, mettre en Unisexe
        if (!wasLinked) {
            linkToCategory(node, "Unisexe");
            wasLinked = true;
        }

        logger.info("Produit '{}' lié aux catégories", product.getName());
        return new SyncResult(wasCreated, wasLinked);
    }

    /**
     * Lie un produit à une catégorie via Cypher direct
     */
    public void linkToCategory(ProductNode node, String categoryName) {
        try {
            categoryRepository.linkProductToCategory(categoryName, node.getMysqlProductId());
            logger.info("Produit {} lié à la catégorie '{}'", node.getMysqlProductId(), categoryName);
        } catch (Exception e) {
            logger.error("Erreur liaison catégorie: {}", e.getMessage());
        }
    }

    /**
     * Lie un produit spécifique à une catégorie
     */
    public boolean linkProductToCategory(Long productId, String categoryName) {
        // Vérifier que la catégorie existe
        if (categoryRepository.findByName(categoryName).isEmpty()) {
            logger.error("Catégorie '{}' non trouvée", categoryName);
            return false;
        }

        // Vérifier/créer le ProductNode
        Optional<ProductNode> nodeOpt = productNodeRepository.findByMysqlProductId(productId);
        if (nodeOpt.isEmpty()) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                logger.error("Produit {} non trouvé dans MySQL", productId);
                return false;
            }

            ProductNode node = new ProductNode(productId, product.getName(), "SKU-" + productId);
            productNodeRepository.save(node);
        }

        // Créer la relation via Cypher
        try {
            categoryRepository.linkProductToCategory(categoryName, productId);
            logger.info("Produit {} lié à '{}'", productId, categoryName);
            return true;
        } catch (Exception e) {
            logger.error("Erreur: {}", e.getMessage());
            return false;
        }
    }
}
