package parfumerie.parfilya.repositories.msql;

import parfumerie.parfilya.models.mysql.Address;
import parfumerie.parfilya.models.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUser(User user);
}