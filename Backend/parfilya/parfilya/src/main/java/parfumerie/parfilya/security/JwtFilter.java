package parfumerie.parfilya.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import parfumerie.parfilya.models.mysql.User;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        //  Pas de token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            //  Vérification du token
            if (!jwtUtil.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtUtil.extractUsername(token);
            String rolesStr = jwtUtil.extractRole(token);
            String permissionsStr = jwtUtil.extractPermissions(token);

            // ⚠Eviter écrasement contexte
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
                
                // Ajouter les rôles
                if (rolesStr != null && !rolesStr.isEmpty()) {
                    String[] roles = rolesStr.split(",");
                    for (String role : roles) {
                        role = role.trim();
                        if (!role.isEmpty()) {
                            // évite ROLE_ROLE_ADMIN
                            if (role.startsWith("ROLE_")) {
                                authorities.add(new SimpleGrantedAuthority(role));
                            } else {
                                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                            }
                        }
                    }
                    }


                // Ajouter les permissions
                if (permissionsStr != null && !permissionsStr.isEmpty()) {
                    String[] permissions = permissionsStr.split(",");
                    for (String permission : permissions) {
                        if (!permission.trim().isEmpty()) {
                            authorities.add(new SimpleGrantedAuthority(permission.trim()));
                        }
                    }
                }

                User userDetails = new User(email, "", authorities);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                //  Injection dans Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    //  Ignorer login / register
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth");
    }
}
