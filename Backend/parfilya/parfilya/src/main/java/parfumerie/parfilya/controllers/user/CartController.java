package parfumerie.parfilya.controllers.user;

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

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        User user = getCurrentUser();
        Cart cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartItem> addToCart(@RequestBody AddToCartRequest request) {
        User user = getCurrentUser();
        Cart cart = cartService.getCartByUser(user);

        Product product = productService.findById(request.getProductId());
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(request.getQuantity());

        return ResponseEntity.ok(cartItemService.addItem(item));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long itemId,
                                                   @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartItemService.updateQuantity(itemId, request.getQuantity()));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
        cartItemService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        User user = getCurrentUser();
        Cart cart = cartService.getCartByUser(user);
        cartService.deleteCart(cart.getId());
        return ResponseEntity.noContent().build();
    }
}
