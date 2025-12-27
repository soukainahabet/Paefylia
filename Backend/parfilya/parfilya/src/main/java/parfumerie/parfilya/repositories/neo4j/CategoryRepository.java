package parfumerie.parfilya.repositories.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import parfumerie.parfilya.models.neo4j.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends Neo4jRepository<Category, String> {

    Optional<Category> findByName(String name);

    @Query("MATCH (c:Category)-[:HAS_PRODUCT]->(p:Product) WHERE p.mysqlProductId = $productId RETURN c")
    List<Category> findCategoriesByProductId(Long productId);

    @Query("MATCH (c:Category) RETURN c ORDER BY c.name")
    List<Category> findAllOrderByName();

    @Query("MATCH (c:Category {name: $categoryName}), (p:Product {mysqlProductId: $productId}) " +
           "MERGE (c)-[:HAS_PRODUCT]->(p)")
    void linkProductToCategory(String categoryName, Long productId);
}
