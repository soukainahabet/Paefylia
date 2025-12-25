package parfumerie.parfilya.repositories.msql;

import parfumerie.parfilya.models.mysql.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}