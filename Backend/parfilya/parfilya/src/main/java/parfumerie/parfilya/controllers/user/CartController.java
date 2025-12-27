package parfumerie.parfilya.controllers.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import parfumerie.parfilya.dto.cart.AddToCartRequest;
import parfumerie.parfilya.dto.cart.UpdateCartItemRequest;
import parfumerie.parfilya.models.mysql.Cart;
import parfumerie.parfilya.models.mysql.CartItem;
import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.services.user.CartService;
import parfumerie.parfilya.services.user.CartItemService;
import parfumerie.parfilya.services.user.ProductService;
import parfumerie.parfilya.services.user.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final ProductService productService;

    public CartController(CartService cartService, CartItemService cartItemService,
                         UserService userService, ProductService productService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.userService = userService;
        this.productService = productService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) auth.getPrincipal()).getEmail();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * GET /api/cart - Afficher le panier de l'utilisateur
     */
    @GetMapping
    public ResponseEntity<Cart> getCart() {
        User user = getCurrentUser();
        Cart cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(cart);
    }

    /**
     * GET /api/cart/summary - Résumé du panier (nombre d'items + total)
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getCartSummary() {
        User user = getCurrentUser();
        Cart cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(Map.of(
                "itemCount", cart.getItemCount(),
                "total", cart.getTotal()
        ));
    }

    /**
     * POST /api/cart/items - Ajouter un produit au panier
     */
    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request) {
        User user = getCurrentUser();
        Cart cart = cartService.getCartByUser(user);

        // Vérifier que le produit existe
        Product product;
        try {
            product = productService.findById(request.getProductId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", "Produit non trouvé"
            ));
        }

        // Vérifier le stock disponible
        int requestedQty = request.getQuantity() != null ? request.getQuantity() : 1;
        if (product.getStock() != null && product.getStock() < requestedQty) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", "Stock insuffisant. Disponible: " + product.getStock()
            ));
        }

        // Ajouter au panier (gère les doublons automatiquement)
        CartItem item = cartService.addToCart(cart, product, requestedQty);
        return ResponseEntity.ok(item);
    }

    /**
     * PUT /api/cart/items/{itemId} - Modifier la quantité d'un item
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long itemId,
                                            @Valid @RequestBody UpdateCartItemRequest request) {
        if (request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", true,
                    "message", "La quantité doit être supérieure à 0"
            ));
        }
        return ResponseEntity.ok(cartItemService.updateQuantity(itemId, request.getQuantity()));
    }

    /**
     * DELETE /api/cart/items/{itemId} - Supprimer un item du panier
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
        cartItemService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/cart - Vider le panier
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        User user = getCurrentUser();
        Cart cart = cartService.getCartByUser(user);
        cartService.clearCart(cart);
        return ResponseEntity.noContent().build();
    }
}
