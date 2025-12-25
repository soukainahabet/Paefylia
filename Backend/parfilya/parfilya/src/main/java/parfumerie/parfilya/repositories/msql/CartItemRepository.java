package parfumerie.parfilya.repositories.msql;

import parfumerie.parfilya.models.mysql.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
