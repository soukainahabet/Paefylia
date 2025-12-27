package parfumerie.parfilya.models.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.HashSet;
import java.util.Set;

@Node("Brand")
public class Brand {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    private String name;
    private String country;
    private String logoUrl;

    @Relationship(type = "PRODUCES", direction = Relationship.Direction.OUTGOING)
    private Set<ProductNode> products = new HashSet<>();

    public Brand() {}

    public Brand(String name, String country) {
        this.name = name;
        this.country = country;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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
