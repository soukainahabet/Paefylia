package parfumerie.parfilya.controllers.admin;

import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.services.admin.AdminProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:3001")
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
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
}
