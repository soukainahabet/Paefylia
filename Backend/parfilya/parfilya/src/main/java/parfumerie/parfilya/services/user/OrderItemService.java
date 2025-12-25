package parfumerie.parfilya.services.user;


import parfumerie.parfilya.models.mysql.OrderItem;
import parfumerie.parfilya.repositories.msql.OrderItemRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem create(OrderItem item) {
        return orderItemRepository.save(item);
    }

    public void delete(Long id) {
        orderItemRepository.deleteById(id);
    }
}

