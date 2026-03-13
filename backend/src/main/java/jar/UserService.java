package jar;

import jar.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for User operations.
 * Handles business logic for user registration, login, and profile management.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor injection - preferred over field injection.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user with email and password.
     * Throws RuntimeException if email already exists.
     */
    public User registerUser(String name, String email, String password, String phone) {
        validateRegistrationInput(name, email, password);

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered. Please login instead.");
        }

        User newUser = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .phone(phone)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(newUser);
        logger.info("New user registered successfully: {}", email);
        return savedUser;
    }

    /**
     * Authenticate user by email and password.
     * Throws RuntimeException if credentials are invalid or account is disabled.
     */
    public User loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Failed login attempt for user: {}", email);
            throw new RuntimeException("Invalid email or password");
        }

        logger.info("User logged in successfully: {}", email);
        return user;
    }

    /**
     * Get user by ID.
     */
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Get user by email.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Update user profile (name and email).
     */
    public User updateUserProfile(Long userId, String name, String email, String phone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }

        if (email != null && !email.trim().isEmpty()) {
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(email);
        }

        if (phone != null) {
            user.setPhone(phone);
        }

        User updatedUser = userRepository.save(user);
        logger.info("User profile updated: {}", userId);
        return updatedUser;
    }

    /**
     * Change user password.
     */
    public User changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);
        logger.info("Password changed for user: {}", userId);
        return updatedUser;
    }

    /**
     * Deactivate user account.
     */
    public User deactivateAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(false);
        User deactivatedUser = userRepository.save(user);
        logger.info("User account deactivated: {}", userId);
        return deactivatedUser;
    }

    /**
     * Add a skill to user.
     */
    public User addSkillToUser(Long userId, Skill skill) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getSkills().add(skill);
        User updatedUser = userRepository.save(user);
        logger.info("Skill added to user {}: {}", userId, skill.getName());
        return updatedUser;
    }

    /**
     * Remove a skill from user.
     */
    public User removeSkillFromUser(Long userId, Skill skill) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getSkills().remove(skill);
        User updatedUser = userRepository.save(user);
        logger.info("Skill removed from user {}: {}", userId, skill.getName());
        return updatedUser;
    }

    /**
     * Get user skills.
     */
    public java.util.Set<Skill> getUserSkills(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getSkills();
    }

    /**
     * Convert User entity to UserDTO (safe for API responses).
     */
    public UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().name() : "USER")
                .isActive(user.isActive())
                .build();
    }

    /**
     * Validate registration input.
     */
    private void validateRegistrationInput(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
    }
}
