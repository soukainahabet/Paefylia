package parfumerie.parfilya.controllers.user;

import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.services.user.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ✅ GET : voir tous les parfums
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    // ✅ GET : détails d’un parfum
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }

    // ✅ GET : filtrer par marque
    @GetMapping("/brand/{brand}")
    public List<Product> getByBrand(@PathVariable String brand) {
        return productService.findByBrand(brand);
    }

    // ✅ GET : rechercher un parfum
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String q) {
        return productService.search(q);
    }

    // ❌ DELETE : réservé à ADMIN normalement
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
