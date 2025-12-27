package parfumerie.parfilya.models.mysql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();

    // Getters & Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    // Méthodes utilitaires
    public int getItemCount() {
        if (items == null || items.isEmpty()) return 0;
        return items.stream()
                .filter(item -> item.getQuantity() != null)
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public Double getTotal() {
        if (items == null || items.isEmpty()) return 0.0;
        return items.stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getPrice() != null
                        && item.getQuantity() != null)
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}
