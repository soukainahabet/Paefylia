package parfumerie.parfilya.dto.product;

import parfumerie.parfilya.models.mongo.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO combinant les données produit de MySQL, MongoDB et Neo4j
 */
public class ProductFullDetailsDTO {

    // MySQL data
    private Long id;
    private String name;
    private String brand;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String description;

    // MongoDB data (ProductDetails)
    private String longDescription;
    private List<String> ingredients = new ArrayList<>();
    private List<String> notes = new ArrayList<>();
    private String usage;
    private List<String> images = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();
    private Double averageRating;

    // Neo4j data
    private List<String> categories = new ArrayList<>();

    public ProductFullDetailsDTO() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLongDescription() { return longDescription; }
    public void setLongDescription(String longDescription) { this.longDescription = longDescription; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getNotes() { return notes; }
    public void setNotes(List<String> notes) { this.notes = notes; }

    public String getUsage() { return usage; }
    public void setUsage(String usage) { this.usage = usage; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
}
