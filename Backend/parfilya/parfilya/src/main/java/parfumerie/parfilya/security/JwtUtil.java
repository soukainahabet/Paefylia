package parfumerie.parfilya.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import parfumerie.parfilya.models.mysql.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "parfumerie-secret-key-256-bits-minimum!!";
    private static final long EXPIRATION_TIME = 86400000; // 24h

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // ================== GENERATE TOKEN ==================
    public String generateToken(User user) {
        String roles = user.getRoles() != null ? String.join(",", user.getRoles()) : "";
        String permissions = user.getPermissions() != null ? String.join(",", user.getPermissions()) : "";
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roles);
        claims.put("permissions", permissions);
        
        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail() != null ? user.getEmail() : user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    // ================== EXTRACT CLAIMS ==================
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
    
    public String extractPermissions(String token) {
        return extractAllClaims(token).get("permissions", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
