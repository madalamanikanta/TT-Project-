package jar;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple admin controller that serves the admin UI pages and provides a login endpoint
 * that issues a JWT token in an HttpOnly cookie for browser-based access.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AdminController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Redirects to the admin dashboard when already authenticated, otherwise shows the login page.
     */
    @GetMapping
    public RedirectView adminRoot(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return new RedirectView("/admin/dashboard");
        }
        return new RedirectView("/admin/login.html");
    }

    /**
     * Serve a very simple dashboard JSON response for demo purposes.
     * In a real application you would serve an SPA or proper server-rendered UI.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "message", "Welcome to the admin dashboard",
                "user", authentication.getName(),
                "roles", roles
        ));
    }

    /**
     * Simple admin login endpoint that issues a JWT cookie when the supplied credentials are valid
     * and belong to the configured admin user.
     */
    @PostMapping("/login")
    public RedirectView login(@RequestBody LoginRequest request,
                              HttpServletResponse response) {
        User user = userService.loginUser(request.getEmail(), request.getPassword());
        if (user.getRole() != User.Role.ADMIN) {
            return new RedirectView("/admin/login.html?error=not-admin");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // SameSite=Lax allows direct navigation but blocks most cross-site requests
        cookie.setSecure(false);
        response.addCookie(cookie);

        logger.info("Admin logged in: {}", user.getEmail());
        return new RedirectView("/admin/dashboard.html");
    }

    /**
     * Logs out an admin by clearing the JWT cookie.
     */
    @PostMapping("/logout")
    public RedirectView logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return new RedirectView("/admin/login.html");
    }
}
