package parfumerie.parfilya.controllers.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import parfumerie.parfilya.models.mysql.User;
import parfumerie.parfilya.services.admin.AdminService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminService adminService;

    public AdminUserController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * GET /api/admin/users
     * Récupérer tous les utilisateurs
     * Permission: ADMIN ou READ
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * GET /api/admin/users/{id}
     * Récupérer un utilisateur par ID
     * Permission: ADMIN ou READ
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return adminService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/admin/users
     * Créer un nouvel utilisateur
     * Permission: ADMIN avec WRITE
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('WRITE')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(adminService.createUser(user));
    }

    /**
     * PUT /api/admin/users/{id}
     * Mettre à jour un utilisateur
     * Permission: ADMIN avec WRITE
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('WRITE')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(adminService.updateUser(id, user));
    }

    /**
     * PUT /api/admin/users/{id}/role
     * Changer le rôle d'un utilisateur (USER ↔ ADMIN)
     * Permission: ADMIN avec WRITE
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('WRITE')")
    public ResponseEntity<User> changeUserRole(
            @PathVariable Long id,
            @RequestParam String role) {
        return ResponseEntity.ok(adminService.changeUserRole(id, role));
    }

    /**
     * POST /api/admin/users/{id}/permissions
     * Ajouter une permission à un utilisateur
     * Permission: ADMIN avec WRITE
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('WRITE')")
    public ResponseEntity<User> addPermission(
            @PathVariable Long id,
            @RequestParam String permission) {
        return ResponseEntity.ok(adminService.addPermission(id, permission));
    }

    /**
     * DELETE /api/admin/users/{id}/permissions
     * Retirer une permission d'un utilisateur
     * Permission: ADMIN avec WRITE
     */
    @DeleteMapping("/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('WRITE')")
    public ResponseEntity<User> removePermission(
            @PathVariable Long id,
            @RequestParam String permission) {
        return ResponseEntity.ok(adminService.removePermission(id, permission));
    }

    /**
     * DELETE /api/admin/users/{id}
     * Supprimer un utilisateur
     * Permission: ADMIN avec WRITE
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('WRITE')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
