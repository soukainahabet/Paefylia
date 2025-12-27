package parfumerie.parfilya.repositories.msql;

import parfumerie.parfilya.models.mysql.Cart;
import parfumerie.parfilya.models.mysql.CartItem;
import parfumerie.parfilya.models.mysql.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    List<CartItem> findByCart(Cart cart);

    void deleteByCart(Cart cart);
}
