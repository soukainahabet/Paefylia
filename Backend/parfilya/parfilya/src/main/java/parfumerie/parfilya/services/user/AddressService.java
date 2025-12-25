package parfumerie.parfilya.services.user;

import parfumerie.parfilya.models.mysql.Address;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.repositories.msql.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address create(Address address) {
        return addressRepository.save(address);
    }

    public List<Address> getUserAddresses(User user) {
        return addressRepository.findByUser(user);
    }

    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}
