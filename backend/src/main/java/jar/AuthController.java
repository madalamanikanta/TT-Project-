package jar;

import jar.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication endpoints.
 * Handles user registration, login, and profile management.
 */
@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Constructor injection.
     */
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new user.
     * POST /api/auth/register
     * Body: { "name": "John Doe", "email": "john@example.com", "password": "password123" }
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            logger.info("New registration attempt for email: {}", request.getEmail());
            User user = userService.registerUser(request.getName(), request.getEmail(), request.getPassword(), request.getPhone());
            UserDTO userDTO = userService.convertToDTO(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", userDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Login user.
     * POST /api/auth/login
     * Body: { "email": "john@example.com", "password": "password123" }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            logger.info("Login attempt for email: {}", request.getEmail());
            User user = userService.loginUser(request.getEmail(), request.getPassword());
            UserDTO userDTO = userService.convertToDTO(user);
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", userDTO);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Login failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get user profile by ID.
     * GET /api/auth/profile/{userId}
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserDTO userDTO = userService.convertToDTO(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", userDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Failed to fetch profile: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Update user profile.
     * PUT /api/auth/profile/{userId}
     * Body: { "name": "Jane Doe", "email": "jane@example.com" }
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {
        try {
            logger.info("Profile update request for user: {}", userId);
            User user = userService.updateUserProfile(userId, request.getName(), request.getEmail(), request.getPhone());
            UserDTO userDTO = userService.convertToDTO(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("user", userDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Profile update failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Change user password.
     * PUT /api/auth/change-password/{userId}
     * Body: { "oldPassword": "oldpass123", "newPassword": "newpass456" }
     */
    @PutMapping("/change-password/{userId}")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordRequest request) {
        try {
            logger.info("Password change request for user: {}", userId);
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Password change failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Deactivate user account.
     * DELETE /api/auth/deactivate/{userId}
     */
    @DeleteMapping("/deactivate/{userId}")
    public ResponseEntity<Map<String, Object>> deactivateAccount(@PathVariable Long userId) {
        try {
            logger.info("Account deactivation request for user: {}", userId);
            userService.deactivateAccount(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Account deactivation failed: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Health check endpoint.
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
