package parfumerie.parfilya.controllers.user;


import parfumerie.parfilya.models.mysql.CartItem;
import parfumerie.parfilya.services.user.CartItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public CartItem addItem(@RequestBody CartItem item) {
        return cartItemService.addItem(item);
    }

    @PutMapping("/{id}")
    public CartItem updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        return cartItemService.updateQuantity(id, quantity);
    }

    @DeleteMapping("/{id}")
    public void removeItem(@PathVariable Long id) {
        cartItemService.removeItem(id);
    }
}

