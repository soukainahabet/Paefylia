package parfumerie.parfilya.models.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "product_details")
public class ProductDetails {

    @Id
    private String id;

    private Long mysqlProductId;
    private String longDescription;
    private List<String> ingredients = new ArrayList<>();
    private List<String> notes = new ArrayList<>();
    private String usage;
    private List<String> images = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();
    private Double averageRating;

    public ProductDetails() {}

    public ProductDetails(Long mysqlProductId) {
        this.mysqlProductId = mysqlProductId;
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

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
        updateAverageRating();
    }

    private void updateAverageRating() {
        if (reviews.isEmpty()) {
            this.averageRating = 0.0;
        } else {
            this.averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        }
    }
}
