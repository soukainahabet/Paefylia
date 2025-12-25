package parfumerie.parfilya.services.admin;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.repositories.msql.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Récupérer tous les utilisateurs (READ permission)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Récupérer un utilisateur par ID (READ permission)
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Créer un nouvel utilisateur (WRITE permission)
     */
    public User createUser(User user) {
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (user.getUsername() != null && userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // Permissions par défaut si non définies
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.addRole("USER");
        }
        if (user.getPermissions() == null || user.getPermissions().isEmpty()) {
            user.addPermission("READ");
        }
        
        return userRepository.save(user);
    }

    /**
     * Mettre à jour un utilisateur (WRITE permission)
     */
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
                        if (userRepository.existsByEmail(updatedUser.getEmail())) {
                            throw new RuntimeException("Email already exists");
                        }
                        user.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getName() != null) {
                        user.setName(updatedUser.getName());
                    }
                    if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(user.getUsername())) {
                        if (userRepository.existsByUsername(updatedUser.getUsername())) {
                            throw new RuntimeException("Username already exists");
                        }
                        user.setUsername(updatedUser.getUsername());
                    }
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Changer le rôle d'un utilisateur (WRITE permission)
     * Change USER → ADMIN ou ADMIN → USER
     */
    public User changeUserRole(Long id, String newRole) {
        return userRepository.findById(id)
                .map(user -> {
                    // Supprimer tous les rôles existants
                    user.getRoles().clear();
                    
                    // Ajouter le nouveau rôle
                    if ("ADMIN".equalsIgnoreCase(newRole)) {
                        user.addRole("ADMIN");
                        // Les admins ont toutes les permissions
                        if (!user.hasPermission("READ")) {
                            user.addPermission("READ");
                        }
                        if (!user.hasPermission("WRITE")) {
                            user.addPermission("WRITE");
                        }
                    } else {
                        user.addRole("USER");
                        // Les users ont seulement READ par défaut
                        if (!user.hasPermission("READ")) {
                            user.addPermission("READ");
                        }
                        // Retirer WRITE si présent
                        user.getPermissions().remove("WRITE");
                    }
                    
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Ajouter une permission à un utilisateur (WRITE permission)
     */
    public User addPermission(Long id, String permission) {
        return userRepository.findById(id)
                .map(user -> {
                    user.addPermission(permission);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Retirer une permission d'un utilisateur (WRITE permission)
     */
    public User removePermission(Long id, String permission) {
        return userRepository.findById(id)
                .map(user -> {
                    if (user.getPermissions() != null) {
                        user.getPermissions().remove(permission);
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Supprimer un utilisateur (WRITE permission)
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
