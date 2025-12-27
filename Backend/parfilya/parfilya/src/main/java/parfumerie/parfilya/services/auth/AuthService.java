package parfumerie.parfilya.services.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

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
        logger.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Inscription échouée - Email déjà existant: {}", request.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        user.addRole("USER");
        user.addPermission("READ");

        userRepository.save(user);
        logger.info("Inscription réussie pour l'utilisateur: {} (ID: {})", user.getEmail(), user.getId());

        return new AuthResponse(jwtUtil.generateToken(user));
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Tentative de connexion pour l'email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Connexion échouée - Email non trouvé: {}", request.getEmail());
                    return new RuntimeException("Invalid credentials");
                });

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Connexion échouée - Mot de passe incorrect pour: {}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("Connexion réussie pour l'utilisateur: {} (ID: {})", user.getEmail(), user.getId());
        return new AuthResponse(jwtUtil.generateToken(user));
    }
}

