package parfumerie.parfilya.controllers.user;

import parfumerie.parfilya.models.mysql.Address;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.services.user.AddressService;
import parfumerie.parfilya.services.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @PostMapping("/{userId}")
    public Address create(@PathVariable Long userId, @RequestBody Address address) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        address.setUser(user);
        return addressService.create(address);
    }

    @GetMapping("/{userId}")
    public List<Address> getUserAddresses(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return addressService.getUserAddresses(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        addressService.delete(id);
    }
}

