package parfumerie.parfilya.repositories.msql;


import parfumerie.parfilya.models.mysql.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
            String name, String brand
    );
    List<Product> findByBrandIgnoreCase(String brand);
    List<Product> findByNameContainingIgnoreCase(String name);

}