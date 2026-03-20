package jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     */
    public Map<String, Object> register(String name, String email, String password) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate input
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Name is required");
                return response;
            }

            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Email is required");
                return response;
            }

            if (password == null || password.length() < 6) {
                response.put("success", false);
                response.put("error", "Password must be at least 6 characters");
                return response;
            }

            // Check if email already exists
            if (userRepository.existsByEmail(email)) {
                response.put("success", false);
                response.put("error", "Email already registered. Please login instead.");
                logger.warn("Registration attempt with existing email: {}", email);
                return response;
            }

            // Create new user with hashed password
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setRole(User.Role.STUDENT);

            // Save user to database
            User savedUser = userRepository.save(newUser);

            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", savedUser.getId());
            response.put("email", savedUser.getEmail());
            response.put("name", savedUser.getName());

            logger.info("New user registered successfully: {}", email);

        } catch (Exception e) {
            logger.error("Error registering user", e);
            response.put("success", false);
            response.put("error", "Registration failed: " + e.getMessage());
        }

        return response;
    }

    /**
     * Login user with email and password
     */
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate input
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Email is required");
                return response;
            }

            if (password == null || password.isEmpty()) {
                response.put("success", false);
                response.put("error", "Password is required");
                return response;
            }

            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("error", "Invalid email or password");
                logger.warn("Login attempt with non-existent email: {}", email);
                return response;
            }

            User user = userOptional.get();

            // Check if user is active
            if (!user.isActive()) {
                response.put("success", false);
                response.put("error", "Account is disabled");
                logger.warn("Login attempt on disabled account: {}", email);
                return response;
            }

            // Compare passwords using BCrypt
            if (!passwordEncoder.matches(password, user.getPassword())) {
                response.put("success", false);
                response.put("error", "Invalid email or password");
                logger.warn("Failed login attempt for user: {}", email);
                return response;
            }

            // Login successful
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());

            logger.info("User logged in successfully: {}", email);

        } catch (Exception e) {
            logger.error("Error during login", e);
            response.put("success", false);
            response.put("error", "Login failed: " + e.getMessage());
        }

        return response;
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Update user profile
     */
    public Map<String, Object> updateProfile(Long userId, String name, String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("error", "User not found");
                return response;
            }

            User user = userOptional.get();

            // Check if new email is already taken
            if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
                response.put("success", false);
                response.put("error", "Email already in use");
                return response;
            }

            user.setName(name);
            user.setEmail(email);
            User updatedUser = userRepository.save(user);

            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("user", updatedUser);

            logger.info("User profile updated: {}", userId);

        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            response.put("success", false);
            response.put("error", "Update failed: " + e.getMessage());
        }

        return response;
    }

    /**
     * Change password
     */
    public Map<String, Object> changePassword(Long userId, String oldPassword, String newPassword) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("error", "User not found");
                return response;
            }

            User user = userOptional.get();

            // Verify old password
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                response.put("success", false);
                response.put("error", "Current password is incorrect");
                return response;
            }

            // Validate new password
            if (newPassword == null || newPassword.length() < 6) {
                response.put("success", false);
                response.put("error", "New password must be at least 6 characters");
                return response;
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "Password changed successfully");

            logger.info("Password changed for user: {}", userId);

        } catch (Exception e) {
            logger.error("Error changing password", e);
            response.put("success", false);
            response.put("error", "Password change failed: " + e.getMessage());
        }

        return response;
    }

    /**
     * Deactivate user account
     */
    public Map<String, Object> deactivateAccount(Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                response.put("success", false);
                response.put("error", "User not found");
                return response;
            }

            User user = userOptional.get();
            user.setActive(false);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "Account deactivated");

            logger.info("Account deactivated for user: {}", userId);

        } catch (Exception e) {
            logger.error("Error deactivating account", e);
            response.put("success", false);
            response.put("error", "Deactivation failed: " + e.getMessage());
        }

        return response;
    }
}
