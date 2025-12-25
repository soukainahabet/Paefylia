package parfumerie.parfilya.repositories.msql;

import parfumerie.parfilya.models.mysql.Cart;
import parfumerie.parfilya.models.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}