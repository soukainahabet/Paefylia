package parfumerie.parfilya.services.user;

import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.repositories.msql.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User update(Long id, User newUser) {
        return userRepository.findById(id).map(user -> {
            if (newUser.getName() != null) {
                user.setName(newUser.getName());
            }
            if (newUser.getEmail() != null) {
                user.setEmail(newUser.getEmail());
            }
            if (newUser.getUsername() != null) {
                user.setUsername(newUser.getUsername());
            }
            if (newUser.getPassword() != null) {
                user.setPassword(newUser.getPassword());
            }
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
