package parfumerie.parfilya.services.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import parfumerie.parfilya.dto.auth.AuthResponse;
import parfumerie.parfilya.dto.auth.LoginRequest;
import parfumerie.parfilya.dto.auth.RegisterRequest;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.repositories.msql.UserRepository;
import parfumerie.parfilya.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        user.addRole("USER"); // Par défaut, rôle USER
        user.addPermission("READ"); // Permission de base

        userRepository.save(user);

        return new AuthResponse(jwtUtil.generateToken(user));
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return new AuthResponse(jwtUtil.generateToken(user));
    }
}

