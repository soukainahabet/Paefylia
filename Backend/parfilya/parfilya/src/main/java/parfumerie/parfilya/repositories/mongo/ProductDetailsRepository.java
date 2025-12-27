package parfumerie.parfilya.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import parfumerie.parfilya.models.mongo.ProductDetails;

import java.util.Optional;

public interface ProductDetailsRepository extends MongoRepository<ProductDetails, String> {

    Optional<ProductDetails> findByMysqlProductId(Long mysqlProductId);

    void deleteByMysqlProductId(Long mysqlProductId);
}
