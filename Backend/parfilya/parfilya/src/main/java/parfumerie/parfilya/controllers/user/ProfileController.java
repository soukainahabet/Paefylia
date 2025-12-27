package parfumerie.parfilya.controllers.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import parfumerie.parfilya.dto.user.UpdateProfileRequest;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.services.user.UserService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) auth.getPrincipal()).getEmail();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<User> getProfile() {
        User user = getCurrentUser();
        user.setPassword(null); // Don't expose password
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<User> updateProfile(@RequestBody UpdateProfileRequest request) {
        User currentUser = getCurrentUser();

        if (request.getName() != null) {
            currentUser.setName(request.getName());
        }
        if (request.getEmail() != null) {
            currentUser.setEmail(request.getEmail());
        }

        User updated = userService.update(currentUser.getId(), currentUser);
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }
}
