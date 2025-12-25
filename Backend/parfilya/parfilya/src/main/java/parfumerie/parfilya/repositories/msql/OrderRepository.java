package parfumerie.parfilya.repositories.msql;

import parfumerie.parfilya.models.mysql.Order;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.models.mysql.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByStatus(OrderStatus status);
}
