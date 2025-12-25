package parfumerie.parfilya.services.user;

import parfumerie.parfilya.models.mysql.CartItem;
import parfumerie.parfilya.repositories.msql.CartItemRepository;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    public CartItemService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public CartItem addItem(CartItem item) {
        return cartItemRepository.save(item);
    }

    public CartItem updateQuantity(Long id, int quantity) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeItem(Long id) {
        cartItemRepository.deleteById(id);
    }
}
