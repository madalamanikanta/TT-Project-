package jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Creates a single admin user at application startup (if none exists).
 *
 * This satisfies the requirement that only one admin account exists in the database.
 */
@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<User> admins = userRepository.findByRole(User.Role.ADMIN);
        if (!admins.isEmpty()) {
            if (admins.size() > 1) {
                logger.warn("More than one admin user exists ({}). Only the first will be used for admin auth.", admins.size());
            }
            logger.info("Admin account already exists: {}", admins.get(0).getEmail());
            return;
        }

        User admin = User.builder()
                .name("Manikanta")
                .email("madalamanikanta38@gmail.com")
                .password(passwordEncoder.encode("madalamani@7075"))
                .role(User.Role.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(admin);
        logger.info("Created default admin account: {}", admin.getEmail());
    }
}
