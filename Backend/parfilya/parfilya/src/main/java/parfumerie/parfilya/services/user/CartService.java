package parfumerie.parfilya.services.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parfumerie.parfilya.models.mysql.Cart;
import parfumerie.parfilya.models.mysql.CartItem;
import parfumerie.parfilya.models.mysql.Product;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.repositories.msql.CartItemRepository;
import parfumerie.parfilya.repositories.msql.CartRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        return cartRepository.save(cart);
    }

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));
    }

    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    /**
     * Ajoute un produit au panier ou met à jour la quantité si déjà présent
     */
    public CartItem addToCart(Cart cart, Product product, int quantity) {
        // Vérifier si le produit est déjà dans le panier
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            // Mettre à jour la quantité
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        } else {
            // Créer un nouvel item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            return cartItemRepository.save(newItem);
        }
    }

    /**
     * Vide le panier (supprime tous les items)
     */
    @Transactional
    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }
}

