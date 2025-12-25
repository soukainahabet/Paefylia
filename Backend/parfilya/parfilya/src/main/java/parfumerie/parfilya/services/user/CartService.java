package parfumerie.parfilya.services.user;

import parfumerie.parfilya.models.mysql.Cart;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.repositories.msql.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));
    }

    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }
}

