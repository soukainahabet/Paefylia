package parfumerie.parfilya.services.admin;
import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.repositories.msql.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class AdminProductService {
    private final ProductRepository productRepository;

    public AdminProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product add(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product product) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        p.setName(product.getName());
        p.setBrand(product.getBrand());
        p.setPrice(product.getPrice());
        p.setStock(product.getStock());
        p.setImageUrl(product.getImageUrl());
        p.setDescription(product.getDescription());

        return productRepository.save(p);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Product updateStock(Long id, int stock) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        p.setStock(stock);
        return productRepository.save(p);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
}
