package parfumerie.parfilya.services.user;

import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.repositories.msql.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }



    public List<Product> findAll() {
        return productRepository.findAll();
    }

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



    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
