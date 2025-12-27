package parfumerie.parfilya.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import parfumerie.parfilya.models.mongo.ProductDetails;
import parfumerie.parfilya.models.mongo.Review;
import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.models.neo4j.Brand;
import parfumerie.parfilya.models.neo4j.Category;
import parfumerie.parfilya.models.neo4j.ProductNode;
import parfumerie.parfilya.repositories.mongo.ProductDetailsRepository;
import parfumerie.parfilya.repositories.msql.ProductRepository;
import parfumerie.parfilya.repositories.neo4j.BrandRepository;
import parfumerie.parfilya.repositories.neo4j.CategoryRepository;
import parfumerie.parfilya.repositories.neo4j.ProductNodeRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductNodeRepository productNodeRepository;
    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;

    public DataInitializer(CategoryRepository categoryRepository,
                           BrandRepository brandRepository,
                           ProductNodeRepository productNodeRepository,
                           ProductRepository productRepository,
                           ProductDetailsRepository productDetailsRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productNodeRepository = productNodeRepository;
        this.productRepository = productRepository;
        this.productDetailsRepository = productDetailsRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize MongoDB data
        try {
            initProductDetails();
        } catch (Exception e) {
            logger.warn("MongoDB n'est pas disponible, initialisation des détails produits ignorée: {}", e.getMessage());
        }

        // Initialize Neo4j data
        try {
            initCategories();
            initBrands();
            linkProductsToCategories();
        } catch (Exception e) {
            logger.warn("Neo4j n'est pas disponible, initialisation des données ignorée: {}", e.getMessage());
        }
    }

    private void initCategories() {
        if (categoryRepository.count() == 0) {
            logger.info("Initialisation des catégories Neo4j...");

            categoryRepository.save(new Category("Homme", "Parfums pour homme"));
            categoryRepository.save(new Category("Femme", "Parfums pour femme"));
            categoryRepository.save(new Category("Unisexe", "Parfums mixtes"));
            categoryRepository.save(new Category("Luxe", "Parfums haut de gamme"));
            categoryRepository.save(new Category("Sport", "Parfums frais et dynamiques"));
            categoryRepository.save(new Category("Soirée", "Parfums élégants pour les occasions"));
            categoryRepository.save(new Category("Quotidien", "Parfums légers pour tous les jours"));
            categoryRepository.save(new Category("Été", "Parfums frais pour l'été"));
            categoryRepository.save(new Category("Hiver", "Parfums chauds pour l'hiver"));

            logger.info("Catégories créées avec succès !");
        } else {
            logger.info("Catégories déjà présentes dans Neo4j");
        }
    }

    private void initBrands() {
        if (brandRepository.count() == 0) {
            logger.info("Initialisation des marques Neo4j...");

            brandRepository.save(new Brand("Chanel", "France"));
            brandRepository.save(new Brand("Dior", "France"));
            brandRepository.save(new Brand("Guerlain", "France"));
            brandRepository.save(new Brand("Yves Saint Laurent", "France"));
            brandRepository.save(new Brand("Givenchy", "France"));
            brandRepository.save(new Brand("Hermès", "France"));
            brandRepository.save(new Brand("Lancôme", "France"));
            brandRepository.save(new Brand("Giorgio Armani", "Italie"));
            brandRepository.save(new Brand("Versace", "Italie"));
            brandRepository.save(new Brand("Dolce & Gabbana", "Italie"));
            brandRepository.save(new Brand("Calvin Klein", "États-Unis"));
            brandRepository.save(new Brand("Tom Ford", "États-Unis"));
            brandRepository.save(new Brand("Hugo Boss", "Allemagne"));
            brandRepository.save(new Brand("Paco Rabanne", "Espagne"));

            logger.info("Marques créées avec succès !");
        } else {
            logger.info("Marques déjà présentes dans Neo4j");
        }
    }

    private void linkProductsToCategories() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            logger.info("Aucun produit MySQL à lier aux catégories");
            return;
        }

        logger.info("Liaison des produits aux catégories...");

        for (Product product : products) {
            // Vérifier si le ProductNode existe déjà
            Optional<ProductNode> existingNode = productNodeRepository.findByMysqlProductId(product.getId());

            if (existingNode.isEmpty()) {
                // Créer le ProductNode
                ProductNode node = new ProductNode(
                        product.getId(),
                        product.getName(),
                        "SKU-" + product.getId()
                );
                node = productNodeRepository.save(node);

                // Lier à une catégorie selon la marque (exemple simple)
                String brand = product.getBrand() != null ? product.getBrand().toLowerCase() : "";

                // Catégorie par défaut
                Category category = categoryRepository.findByName("Unisexe").orElse(null);

                // Logique simple de catégorisation
                if (brand.contains("homme") || brand.contains("boss") || brand.contains("armani")) {
                    category = categoryRepository.findByName("Homme").orElse(category);
                } else if (brand.contains("chanel") || brand.contains("dior") || brand.contains("guerlain")) {
                    category = categoryRepository.findByName("Femme").orElse(category);
                    // Aussi luxe
                    Category luxe = categoryRepository.findByName("Luxe").orElse(null);
                    if (luxe != null) {
                        luxe.addProduct(node);
                        categoryRepository.save(luxe);
                    }
                }

                if (category != null) {
                    category.addProduct(node);
                    categoryRepository.save(category);
                }

                logger.info("Produit '{}' lié aux catégories", product.getName());
            }
        }

        logger.info("Liaison des produits terminée !");
    }

    private void initProductDetails() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            logger.info("Aucun produit MySQL pour créer les détails MongoDB");
            return;
        }

        logger.info("Initialisation des détails produits MongoDB...");

        // Données de démonstration pour les parfums
        String[] topNotes = {"Bergamote", "Citron", "Mandarine", "Pamplemousse", "Orange", "Lavande", "Menthe"};
        String[] heartNotes = {"Rose", "Jasmin", "Iris", "Pivoine", "Ylang-ylang", "Muguet", "Tubéreuse"};
        String[] baseNotes = {"Bois de santal", "Musc", "Vanille", "Ambre", "Vétiver", "Cèdre", "Patchouli"};
        String[] ingredients = {"Alcool denat.", "Parfum (Fragrance)", "Aqua (Water)", "Limonène", "Linalool", "Coumarine", "Citronellol"};

        String[] reviewAuthors = {"Marie D.", "Pierre L.", "Sophie M.", "Jean-Claude R.", "Isabelle F.", "François B."};
        String[] reviewComments = {
            "Un parfum exceptionnel, je le recommande vivement !",
            "Très bonne tenue, des compliments toute la journée.",
            "Élégant et raffiné, parfait pour les occasions spéciales.",
            "Un classique intemporel, il fait partie de ma collection.",
            "Sillage agréable sans être entêtant, parfait au quotidien."
        };

        Random random = new Random();

        for (Product product : products) {
            // Vérifier si les détails existent déjà
            Optional<ProductDetails> existingOpt = productDetailsRepository.findByMysqlProductId(product.getId());

            // Créer ou récupérer les détails existants
            ProductDetails details;
            boolean needsUpdate = false;

            if (existingOpt.isEmpty()) {
                details = new ProductDetails(product.getId());
                needsUpdate = true;
            } else {
                details = existingOpt.get();
                // Vérifier si les données enrichies sont manquantes
                if (details.getLongDescription() == null || details.getLongDescription().isEmpty()) {
                    needsUpdate = true;
                }
            }

            if (needsUpdate) {

                // Description longue
                details.setLongDescription(
                    "Découvrez " + product.getName() + " de " + product.getBrand() + ", " +
                    "une fragrance captivante qui incarne l'élégance et le raffinement. " +
                    "Ce parfum d'exception vous transporte dans un univers de sensations uniques, " +
                    "où chaque note révèle une facette de votre personnalité. " +
                    "Idéal pour toutes les occasions, il laisse un sillage inoubliable."
                );

                // Notes de parfum (3 de chaque)
                details.setNotes(Arrays.asList(
                    "Note de tête: " + topNotes[random.nextInt(topNotes.length)] + ", " + topNotes[random.nextInt(topNotes.length)],
                    "Note de cœur: " + heartNotes[random.nextInt(heartNotes.length)] + ", " + heartNotes[random.nextInt(heartNotes.length)],
                    "Note de fond: " + baseNotes[random.nextInt(baseNotes.length)] + ", " + baseNotes[random.nextInt(baseNotes.length)]
                ));

                // Ingrédients
                details.setIngredients(Arrays.asList(
                    ingredients[0], ingredients[1], ingredients[2],
                    ingredients[random.nextInt(ingredients.length)]
                ));

                // Usage
                details.setUsage("Vaporiser sur les points de pulsation : poignets, cou, derrière les oreilles. " +
                    "Pour une tenue optimale, appliquer après la douche sur peau hydratée.");

                // Images (URLs de démonstration)
                details.setImages(Arrays.asList(
                    "https://images.unsplash.com/photo-1541643600914-78b084683601?w=800",
                    "https://images.unsplash.com/photo-1594035910387-fea47794261f?w=800",
                    "https://images.unsplash.com/photo-1587017539504-67cfbddac569?w=800"
                ));

                // Ajouter quelques avis de démonstration seulement si aucun avis n'existe
                if (details.getReviews() == null || details.getReviews().isEmpty()) {
                    int numReviews = 2 + random.nextInt(3); // 2-4 avis
                    for (int i = 0; i < numReviews; i++) {
                        Review review = new Review();
                        review.setUserId((long) (i + 1));
                        review.setUserName(reviewAuthors[random.nextInt(reviewAuthors.length)]);
                        review.setRating(4 + random.nextInt(2)); // 4 ou 5 étoiles
                        review.setComment(reviewComments[random.nextInt(reviewComments.length)]);
                        review.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
                        details.addReview(review);
                    }
                }

                productDetailsRepository.save(details);
                logger.info("Détails MongoDB créés/mis à jour pour le produit: {}", product.getName());
            }
        }

        logger.info("Initialisation des détails produits MongoDB terminée !");
    }
}
