package parfumerie.parfilya.services.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import parfumerie.parfilya.dto.product.ProductFullDetailsDTO;
import parfumerie.parfilya.models.mongo.ProductDetails;
import parfumerie.parfilya.models.mongo.Review;
import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.models.neo4j.Category;
import parfumerie.parfilya.models.neo4j.ProductNode;
import parfumerie.parfilya.repositories.mongo.ProductDetailsRepository;
import parfumerie.parfilya.repositories.msql.ProductRepository;
import parfumerie.parfilya.repositories.neo4j.CategoryRepository;
import parfumerie.parfilya.repositories.neo4j.ProductNodeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final CategoryRepository categoryRepository;
    private final ProductNodeRepository productNodeRepository;

    public ProductService(ProductRepository productRepository,
                          ProductDetailsRepository productDetailsRepository,
                          CategoryRepository categoryRepository,
                          ProductNodeRepository productNodeRepository) {
        this.productRepository = productRepository;
        this.productDetailsRepository = productDetailsRepository;
        this.categoryRepository = categoryRepository;
        this.productNodeRepository = productNodeRepository;
    }

    // ============ LISTE DES PRODUITS ============

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Page<Product> findAllPaginated(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable);
    }

    // ============ RECHERCHE ============

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> findByBrand(String brand) {
        return productRepository.findByBrandIgnoreCase(brand);
    }

    public List<Product> search(String keyword) {
        return productRepository
                .findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(keyword, keyword);
    }

    public List<Product> findByCategory(String categoryName) {
        try {
            List<ProductNode> productNodes = productNodeRepository.findProductsByCategory(categoryName);
            List<Long> mysqlIds = productNodes.stream()
                    .map(ProductNode::getMysqlProductId)
                    .collect(Collectors.toList());

            if (mysqlIds.isEmpty()) {
                return new ArrayList<>();
            }
            return productRepository.findAllById(mysqlIds);
        } catch (Exception e) {
            // Neo4j non disponible
            return new ArrayList<>();
        }
    }

    // ============ DETAILS COMPLETS (MySQL + MongoDB + Neo4j) ============

    public ProductFullDetailsDTO getFullDetails(Long productId) {
        // 1. Données MySQL
        Product product = findById(productId);

        ProductFullDetailsDTO dto = new ProductFullDetailsDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        dto.setDescription(product.getDescription());

        // 2. Données MongoDB (enrichissement)
        try {
            Optional<ProductDetails> detailsOpt = productDetailsRepository.findByMysqlProductId(productId);
            if (detailsOpt.isPresent()) {
                ProductDetails details = detailsOpt.get();
                dto.setLongDescription(details.getLongDescription());
                dto.setIngredients(details.getIngredients());
                dto.setNotes(details.getNotes());
                dto.setUsage(details.getUsage());
                dto.setImages(details.getImages());
                dto.setReviews(details.getReviews());
                dto.setAverageRating(details.getAverageRating());
            }
        } catch (Exception e) {
            // MongoDB non disponible - on continue sans les détails enrichis
        }

        // 3. Données Neo4j (catégories) - optionnel
        try {
            List<Category> categories = categoryRepository.findCategoriesByProductId(productId);
            dto.setCategories(categories.stream()
                    .map(Category::getName)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            // Neo4j non disponible - on continue sans les catégories
            dto.setCategories(new ArrayList<>());
        }

        return dto;
    }

    // ============ REVIEWS ============

    public ProductDetails addReview(Long productId, Long userId, String userName, Integer rating, String comment) {
        // Vérifier que le produit existe
        findById(productId);

        // Récupérer ou créer ProductDetails
        ProductDetails details = productDetailsRepository.findByMysqlProductId(productId)
                .orElseGet(() -> {
                    ProductDetails newDetails = new ProductDetails(productId);
                    return productDetailsRepository.save(newDetails);
                });

        // Ajouter la review
        Review review = new Review(userId, userName, rating, comment);
        details.addReview(review);

        return productDetailsRepository.save(details);
    }

    public List<Review> getReviews(Long productId) {
        return productDetailsRepository.findByMysqlProductId(productId)
                .map(ProductDetails::getReviews)
                .orElse(new ArrayList<>());
    }

    // ============ CATEGORIES ============

    public List<Category> getAllCategories() {
        try {
            return categoryRepository.findAllOrderByName();
        } catch (Exception e) {
            // Neo4j non disponible
            return new ArrayList<>();
        }
    }
}
