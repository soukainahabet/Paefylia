package parfumerie.parfilya.controllers.user;

import parfumerie.parfilya.models.mysql.Order;
import parfumerie.parfilya.models.mysql.OrderStatus;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.services.user.OrderService;
import parfumerie.parfilya.services.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/{userId}")
    public Order create(@PathVariable Long userId, @RequestBody Order order) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.setUser(user);
        return orderService.create(order);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderService.findByUser(user);
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id,
                              @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}
