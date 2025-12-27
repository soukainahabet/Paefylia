package parfumerie.parfilya.controllers.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import parfumerie.parfilya.dto.order.CreateOrderRequest;
import parfumerie.parfilya.models.mongo.OrderHistory;
import parfumerie.parfilya.models.mysql.*;
import parfumerie.parfilya.services.user.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final AddressService addressService;

    public OrderController(OrderService orderService, UserService userService,
                          CartService cartService, AddressService addressService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.addressService = addressService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) auth.getPrincipal()).getEmail();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * GET /api/orders - Liste des commandes de l'utilisateur
     */
    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        User user = getCurrentUser();
        return ResponseEntity.ok(orderService.findByUser(user));
    }

    /**
     * GET /api/orders/{id} - Détail d'une commande
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        User user = getCurrentUser();
        return orderService.findById(id)
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .map(order -> ResponseEntity.ok((Object) order))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/orders/{id}/history - Historique détaillé d'une commande (MongoDB)
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<?> getOrderHistory(@PathVariable Long id) {
        User user = getCurrentUser();

        // Vérifier que la commande appartient à l'utilisateur
        Order order = orderService.findById(id)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return orderService.getOrderHistory(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/orders/history - Tout l'historique des commandes de l'utilisateur
     */
    @GetMapping("/history")
    public ResponseEntity<List<OrderHistory>> getUserOrderHistory() {
        User user = getCurrentUser();
        return ResponseEntity.ok(orderService.getUserOrderHistory(user.getId()));
    }

    /**
     * POST /api/orders - Créer une commande depuis le panier
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        User user = getCurrentUser();

        // 1. Vérifier l'adresse
        if (request.getAddressId() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", "L'adresse de livraison est requise"
            ));
        }

        Address address = addressService.findById(request.getAddressId())
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (address == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", "Adresse non trouvée ou non autorisée"
            ));
        }

        // 2. Récupérer le panier
        Cart cart = cartService.getCartByUser(user);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", "Le panier est vide"
            ));
        }

        try {
            // 3. Créer la commande
            Order order = orderService.createOrderFromCart(user, cart, address);

            // 4. Vider le panier
            cartService.clearCart(cart);

            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * PUT /api/orders/{id}/cancel - Annuler une commande
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        User user = getCurrentUser();

        // Vérifier que la commande appartient à l'utilisateur
        Order order = orderService.findById(id)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElse(null);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Order cancelledOrder = orderService.cancelOrder(id);
            return ResponseEntity.ok(cancelledOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * GET /api/orders/{id}/status - Vérifier le statut d'une commande
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getOrderStatus(@PathVariable Long id) {
        User user = getCurrentUser();

        return orderService.findById(id)
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .map(order -> ResponseEntity.ok(Map.of(
                        "orderId", order.getId(),
                        "status", order.getStatus(),
                        "createdAt", order.getCreatedAt()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
