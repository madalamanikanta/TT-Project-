package jar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.http.HttpMethod;
import java.util.Arrays;

/**
 * Security configuration with JWT authentication and CORS support.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AdminOriginFilter adminOriginFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          AdminOriginFilter adminOriginFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.adminOriginFilter = adminOriginFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public auth API (login/register, etc.)
                        .requestMatchers("/api/auth/**").permitAll()
                        // Admin UI entry point and login page (served by backend)
                        .requestMatchers(
                                "/admin",
                                "/admin/login",
                                "/admin/login.html",
                                "/admin/dashboard.html",
                                "/admin/logout"
                        ).permitAll()
                        // Admin API and pages require ADMIN role
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Everything else requires authentication (e.g. student APIs)
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Apply origin check before JWT token parsing (blocks requests from disallowed origins)
        http.addFilterBefore(adminOriginFilter, JwtAuthenticationFilter.class);
        // Then apply JWT authentication for all protected endpoints
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Admin routes must only be accessed from backend origin (localhost:8080)
        // This prevents the frontend dev server (localhost:5173) from making CORS/XHR requests to /admin.
        CorsConfiguration adminCors = new CorsConfiguration();
        adminCors.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        adminCors.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        adminCors.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        adminCors.setAllowCredentials(true);
        source.registerCorsConfiguration("/admin/**", adminCors);

        // Public APIs (used by student frontend) can be called from dev frontend a few common ports.
        CorsConfiguration apiCors = new CorsConfiguration();
        apiCors.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000"
        ));
        apiCors.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        apiCors.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        apiCors.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", apiCors);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
