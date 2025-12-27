package parfumerie.parfilya.controllers.admin;

import org.springframework.http.ResponseEntity;
import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.services.admin.AdminProductService;
import parfumerie.parfilya.services.admin.Neo4jDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final Neo4jDataService neo4jDataService;

    public AdminProductController(AdminProductService adminProductService,
                                   Neo4jDataService neo4jDataService) {
        this.adminProductService = adminProductService;
        this.neo4jDataService = neo4jDataService;
    }

    // ➕ Ajouter un parfum
    @PostMapping
    public Product add(@RequestBody Product product) {
        return adminProductService.add(product);
    }

    // ✏️ Modifier un parfum
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return adminProductService.update(id, product);
    }

    // ❌ Supprimer
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminProductService.delete(id);
    }

    // 📦 Modifier le stock
    @PatchMapping("/{id}/stock")
    public Product updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body
    ) {
        return adminProductService.updateStock(id, body.get("stock"));
    }

    // 📄 Lister tous les produits
    @GetMapping
    public List<Product> getAll() {
        return adminProductService.findAll();
    }

    // 🔍 Recherche
    @GetMapping("/search")
    public List<Product> search(@RequestParam String q) {
        return adminProductService.search(q);
    }

    // ============ SYNC NEO4J ============

    /**
     * POST /api/admin/products/sync-neo4j
     * Synchronise tous les produits MySQL vers Neo4j avec catégories
     */
    @PostMapping("/sync-neo4j")
    public ResponseEntity<Map<String, Object>> syncToNeo4j() {
        Map<String, Object> result = neo4jDataService.syncAllProducts();
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/admin/products/{id}/link-category?category=Homme
     * Lie un produit à une catégorie spécifique
     */
    @PostMapping("/{id}/link-category")
    public ResponseEntity<Map<String, Object>> linkToCategory(
            @PathVariable Long id,
            @RequestParam String category) {
        boolean success = neo4jDataService.linkProductToCategory(id, category);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "productId", id,
                "category", category
        ));
    }
}
