package parfumerie.parfilya.controllers.user;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import parfumerie.parfilya.dto.product.AddReviewRequest;
import parfumerie.parfilya.dto.product.ProductFullDetailsDTO;
import parfumerie.parfilya.models.mongo.ProductDetails;
import parfumerie.parfilya.models.mongo.Review;
import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.models.neo4j.Category;
import parfumerie.parfilya.services.user.ProductService;
import parfumerie.parfilya.services.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    // ============ LISTE DES PRODUITS ============

    /**
     * GET /api/products - Liste tous les parfums
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    /**
     * GET /api/products/paginated - Liste paginée des parfums
     * @param page numéro de page (défaut: 0)
     * @param size taille de page (défaut: 12)
     * @param sortBy champ de tri (défaut: name)
     * @param direction asc ou desc (défaut: asc)
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<Product>> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(productService.findAllPaginated(page, size, sortBy, direction));
    }

    // ============ RECHERCHE ============

    /**
     * GET /api/products/search?q=keyword - Recherche par nom ou marque
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(productService.search(q));
    }

    /**
     * GET /api/products/brand/{brand} - Filtrer par marque
     */
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Product>> getByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(productService.findByBrand(brand));
    }

    /**
     * GET /api/products/category/{category} - Filtrer par catégorie
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    // ============ DETAILS PRODUIT ============

    /**
     * GET /api/products/{id} - Détails basiques d'un parfum (MySQL only)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    /**
     * GET /api/products/{id}/full - Détails complets (MySQL + MongoDB + Neo4j)
     */
    @GetMapping("/{id}/full")
    public ResponseEntity<ProductFullDetailsDTO> getProductFullDetails(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getFullDetails(id));
    }

    // ============ REVIEWS ============

    /**
     * GET /api/products/{id}/reviews - Liste des avis d'un produit
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getReviews(id));
    }

    /**
     * POST /api/products/{id}/reviews - Ajouter un avis (authentifié)
     */
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ProductDetails> addReview(
            @PathVariable Long id,
            @Valid @RequestBody AddReviewRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) auth.getPrincipal()).getEmail();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProductDetails details = productService.addReview(
                id,
                user.getId(),
                user.getName() != null ? user.getName() : user.getEmail(),
                request.getRating(),
                request.getComment()
        );

        return ResponseEntity.ok(details);
    }

    // ============ CATEGORIES ============

    /**
     * GET /api/products/categories - Liste toutes les catégories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }
}
