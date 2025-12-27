package parfumerie.parfilya.models.mysql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ManyToOne
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderStatus status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    // Getters & Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    // Méthodes utilitaires
    public Double getTotal() {
        if (items == null || items.isEmpty()) return 0.0;
        return items.stream()
                .filter(item -> item.getPrice() != null && item.getQuantity() != null)
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public int getItemCount() {
        if (items == null || items.isEmpty()) return 0;
        return items.stream()
                .filter(item -> item.getQuantity() != null)
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
}