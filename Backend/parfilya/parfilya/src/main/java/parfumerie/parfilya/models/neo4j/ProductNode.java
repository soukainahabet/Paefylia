package parfumerie.parfilya.models.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Node("Product")
public class ProductNode {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    private Long mysqlProductId;
    private String name;
    private String sku;

    public ProductNode() {}

    public ProductNode(Long mysqlProductId, String name, String sku) {
        this.mysqlProductId = mysqlProductId;
        this.name = name;
        this.sku = sku;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMysqlProductId() {
        return mysqlProductId;
    }

    public void setMysqlProductId(Long mysqlProductId) {
        this.mysqlProductId = mysqlProductId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
