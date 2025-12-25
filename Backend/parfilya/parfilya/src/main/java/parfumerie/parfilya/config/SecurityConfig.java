package parfumerie.parfilya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import parfumerie.parfilya.security.JwtFilter;
import parfumerie.parfilya.security.AuthEntryPoint;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthEntryPoint authEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtFilter jwtFilter, 
                        AuthEntryPoint authEntryPoint,
                        CorsConfigurationSource corsConfigurationSource) {
        this.jwtFilter = jwtFilter;
        this.authEntryPoint = authEntryPoint;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                //  Désactiver CSRF (API REST)
                .csrf(csrf -> csrf.disable())

                //  CORS activé (utilise CorsConfig)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                //  Stateless (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //  Gestion des erreurs auth
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authEntryPoint)
                )

                //  Autorisations
                .authorizeHttpRequests(auth -> auth

                        //  Public
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/products/**"
                        ).permitAll()

                        // 👤 USER
                        .requestMatchers(
                                "/api/cart/**",
                                "/api/orders/**",
                                "/api/profile/**",
                                "/api/address/**"
                        ).hasRole("USER")

                        //  ADMIN
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Tout le reste
                        .anyRequest().authenticated()
                )

                //  JWT Filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //  Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //  Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
