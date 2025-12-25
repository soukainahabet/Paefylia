package parfumerie.parfilya.controllers.user;


import parfumerie.parfilya.models.mysql.Cart;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.services.user.CartService;
import parfumerie.parfilya.services.user.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartService.getCartByUser(user);
    }

    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
    }
}

