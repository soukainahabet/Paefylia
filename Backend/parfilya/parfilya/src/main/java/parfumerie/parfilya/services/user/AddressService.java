package parfumerie.parfilya.services.user;

import parfumerie.parfilya.models.mysql.Address;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.repositories.msql.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address create(Address address) {
        return addressRepository.save(address);
    }

    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    public List<Address> getUserAddresses(User user) {
        return addressRepository.findByUser(user);
    }

    public Address update(Long id, Address addressRequest) {
        return addressRepository.findById(id).map(address -> {
            if (addressRequest.getFullName() != null) {
                address.setFullName(addressRequest.getFullName());
            }
            if (addressRequest.getStreet() != null) {
                address.setStreet(addressRequest.getStreet());
            }
            if (addressRequest.getCity() != null) {
                address.setCity(addressRequest.getCity());
            }
            if (addressRequest.getPhone() != null) {
                address.setPhone(addressRequest.getPhone());
            }
            return addressRepository.save(address);
        }).orElseThrow(() -> new RuntimeException("Address not found"));
    }

    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}
