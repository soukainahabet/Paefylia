package parfumerie.parfilya.repositories.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import parfumerie.parfilya.models.neo4j.ProductNode;

import java.util.List;
import java.util.Optional;

public interface ProductNodeRepository extends Neo4jRepository<ProductNode, String> {

    Optional<ProductNode> findByMysqlProductId(Long mysqlProductId);

    Optional<ProductNode> findBySku(String sku);

    @Query("MATCH (c:Category {name: $categoryName})-[:HAS_PRODUCT]->(p:Product) RETURN p")
    List<ProductNode> findProductsByCategory(String categoryName);

    @Query("MATCH (b:Brand {name: $brandName})-[:PRODUCES]->(p:Product) RETURN p")
    List<ProductNode> findProductsByBrand(String brandName);

    List<ProductNode> findByNameContainingIgnoreCase(String name);
}
