package parfumerie.parfilya.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import parfumerie.parfilya.models.mongo.OrderHistory;

import java.util.List;
import java.util.Optional;

public interface OrderHistoryRepository extends MongoRepository<OrderHistory, String> {

    Optional<OrderHistory> findByMysqlOrderId(Long mysqlOrderId);

    List<OrderHistory> findByUserId(Long userId);

    List<OrderHistory> findByStatus(String status);

    List<OrderHistory> findByUserIdOrderByOrderDateDesc(Long userId);

    void deleteByMysqlOrderId(Long mysqlOrderId);
}
