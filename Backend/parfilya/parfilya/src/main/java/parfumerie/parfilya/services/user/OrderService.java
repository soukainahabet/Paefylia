package parfumerie.parfilya.services.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parfumerie.parfilya.models.mongo.OrderHistory;
import parfumerie.parfilya.models.mysql.*;
import parfumerie.parfilya.repositories.mongo.OrderHistoryRepository;
import parfumerie.parfilya.repositories.msql.OrderItemRepository;
import parfumerie.parfilya.repositories.msql.OrderRepository;
import parfumerie.parfilya.repositories.msql.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository,
                       OrderHistoryRepository orderHistoryRepository,
                       ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * Crée une commande à partir du panier de l'utilisateur
     */
    @Transactional
    public Order createOrderFromCart(User user, Cart cart, Address address) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Le panier est vide");
        }

        // 1. Créer la commande
        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // 2. Créer les items de commande depuis les items du panier
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // Vérifier le stock
            if (product.getStock() != null && product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Stock insuffisant pour: " + product.getName());
            }

            // Créer l'item de commande
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice()); // Prix au moment de la commande
            orderItems.add(orderItem);

            // Décrémenter le stock
            if (product.getStock() != null) {
                product.setStock(product.getStock() - cartItem.getQuantity());
                productRepository.save(product);
            }
        }

        // 3. Associer les items à la commande et sauvegarder (cascade)
        order.setItems(orderItems);
        order = orderRepository.save(order);

        // 4. Créer l'historique dans MongoDB
        createOrderHistory(order, user);

        return order;
    }

    /**
     * Crée l'entrée d'historique dans MongoDB
     */
    private void createOrderHistory(Order order, User user) {
        OrderHistory history = new OrderHistory(order.getId(), user.getId());
        history.setUserEmail(user.getEmail());
        history.setStatus(order.getStatus().name());
        history.setTotalAmount(BigDecimal.valueOf(order.getTotal()));
        history.setOrderDate(order.getCreatedAt());

        // Ajouter les items
        List<OrderHistory.OrderHistoryItem> historyItems = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            historyItems.add(new OrderHistory.OrderHistoryItem(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    BigDecimal.valueOf(item.getPrice())
            ));
        }
        history.setItems(historyItems);

        // Ajouter les infos de livraison
        if (order.getAddress() != null) {
            OrderHistory.ShippingInfo shippingInfo = new OrderHistory.ShippingInfo();
            shippingInfo.setAddress(order.getAddress().getStreet());
            shippingInfo.setCity(order.getAddress().getCity());
            history.setShippingInfo(shippingInfo);
        }

        // Premier changement de statut
        history.addStatusChange("PENDING", "Commande créée");

        orderHistoryRepository.save(history);
    }

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Met à jour le statut d'une commande et enregistre le changement dans MongoDB
     */
    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus, String comment) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        String oldStatus = order.getStatus().name();
        order.setStatus(newStatus);
        order = orderRepository.save(order);

        // Mettre à jour l'historique MongoDB
        orderHistoryRepository.findByMysqlOrderId(id).ifPresent(history -> {
            history.addStatusChange(newStatus.name(), comment != null ? comment : "Statut mis à jour");
            orderHistoryRepository.save(history);
        });

        return order;
    }

    public Order updateStatus(Long id, OrderStatus status) {
        return updateStatus(id, status, null);
    }

    /**
     * Annule une commande et restaure le stock
     */
    @Transactional
    public Order cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Impossible d'annuler une commande déjà livrée");
        }

        // Restaurer le stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        return updateStatus(id, OrderStatus.CANCELLED, "Commande annulée par l'utilisateur");
    }

    /**
     * Récupère l'historique détaillé d'une commande depuis MongoDB
     */
    public Optional<OrderHistory> getOrderHistory(Long orderId) {
        return orderHistoryRepository.findByMysqlOrderId(orderId);
    }

    /**
     * Récupère tout l'historique des commandes d'un utilisateur
     */
    public List<OrderHistory> getUserOrderHistory(Long userId) {
        return orderHistoryRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}

