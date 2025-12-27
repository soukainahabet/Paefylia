package parfumerie.parfilya.models.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.HashSet;
import java.util.Set;

@Node("Category")
public class Category {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    private String name;
    private String description;

    @Relationship(type = "HAS_PRODUCT", direction = Relationship.Direction.OUTGOING)
    private Set<ProductNode> products = new HashSet<>();

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ProductNode> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductNode> products) {
        this.products = products;
    }

    public void addProduct(ProductNode product) {
        this.products.add(product);
    }
}
