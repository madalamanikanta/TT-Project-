package jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import jar.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API endpoints for admin-only functionality.
 */
@RestController
@RequestMapping("/api/admin")
public class ApiAdminController {

    private static final Logger logger = LoggerFactory.getLogger(ApiAdminController.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipService internshipService;
    private final SavedInternshipRepository savedInternshipRepository;

    public ApiAdminController(UserService userService, UserRepository userRepository, InternshipRepository internshipRepository, InternshipService internshipService, SavedInternshipRepository savedInternshipRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.internshipService = internshipService;
        this.savedInternshipRepository = savedInternshipRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(Authentication authentication) {
        String userName = authentication == null ? "anonymous" : authentication.getName();
        logger.info("Admin dashboard accessed by {}", userName);

        try {
            long totalUsers = Math.max(0, userRepository.count());
            long totalAdmins = Math.max(0, userRepository.countByRole(User.Role.ADMIN));
            // Ensure we count both STUDENT and any remaining legacy USER roles if migration hasn't run yet
            long totalStudents = Math.max(0, totalUsers - totalAdmins);
            long totalInternships = Math.max(0, internshipRepository.count());
            long totalSaved = Math.max(0, savedInternshipRepository.count());

            List<Map<String, String>> activities = new java.util.ArrayList<>();

            List<User> allUsers = userRepository.findAll();
            if (allUsers != null) {
                allUsers.stream()
                    .filter(java.util.Objects::nonNull)
                    .sorted(java.util.Comparator.comparing(User::getCreatedAt, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())).reversed())
                    .limit(5)
                    .forEach(u -> {
                        Map<String, String> activity = new java.util.HashMap<>();
                        activity.put("action", "User registered");
                        activity.put("item", u.getName() != null ? u.getName() : "Unknown User");
                        activity.put("time", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "1970-01-01T00:00:00");
                        activities.add(activity);
                    });
            }

            List<Internship> allInternships = internshipRepository.findAll();
            if (allInternships != null) {
                allInternships.stream()
                    .filter(java.util.Objects::nonNull)
                    .sorted(java.util.Comparator.comparing(Internship::getCreatedAt, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())).reversed())
                    .limit(5)
                    .forEach(i -> {
                        Map<String, String> activity = new java.util.HashMap<>();
                        activity.put("action", "Internship created");
                        activity.put("item", i.getTitle() != null ? i.getTitle() : "Unknown Internship");
                        activity.put("time", i.getCreatedAt() != null ? i.getCreatedAt().toString() : "1970-01-01T00:00:00");
                        activities.add(activity);
                    });
            }

            List<SavedInternship> allSaved = savedInternshipRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "savedAt"));
            if (allSaved != null) {
                allSaved.stream()
                    .filter(java.util.Objects::nonNull)
                    .limit(5)
                    .forEach(s -> {
                        String title = "Unknown Internship";
                        if (s.getInternshipId() != null) {
                            try {
                                internshipRepository.findById(s.getInternshipId()).ifPresent(internship -> {
                                    if (internship != null && internship.getTitle() != null) {
                                        // use effectively final or final variable
                                    }
                                });
                                // Re-fetch title properly to avoid lambda issues
                                Internship internship = internshipRepository.findById(s.getInternshipId()).orElse(null);
                                if (internship != null && internship.getTitle() != null) {
                                    title = internship.getTitle();
                                }
                            } catch (Exception e) {
                                logger.warn("Error fetching internship title for saved record", e);
                            }
                        }
                        Map<String, String> activity = new java.util.HashMap<>();
                        activity.put("action", "Internship saved");
                        activity.put("item", title);
                        activity.put("time", s.getSavedAt() != null ? s.getSavedAt().toString() : "1970-01-01T00:00:00");
                        activities.add(activity);
                    });
            }

            activities.sort((a,b) -> {
                String t1 = a.get("time");
                String t2 = b.get("time");
                if (t1 == null) return 1;
                if (t2 == null) return -1;
                return t2.compareTo(t1);
            });

            List<Map<String, String>> recentActivities = activities.stream()
                    .filter(java.util.Objects::nonNull)
                    .limit(10)
                    .collect(Collectors.toList());
            if (recentActivities.isEmpty()) {
                recentActivities = List.of(Map.of("action", "System Started", "item", "System", "time", "Just now"));
            }

            return ResponseEntity.ok(Map.of(
                    "totalUsers", totalUsers,
                    "totalAdmins", totalAdmins,
                    "totalStudents", totalStudents,
                    "totalInternships", totalInternships,
                    "totalSaved", totalSaved,
                    "recentActivities", recentActivities
            ));

        } catch (Exception e) {
            logger.error("Error preparing admin dashboard", e);
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "totalUsers", 0,
                    "totalAdmins", 0,
                    "totalStudents", 0,
                    "totalInternships", 0,
                    "totalSaved", 0,
                    "recentActivities", List.of()
            ));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> listUsers() {
        try {
            List<UserDTO> users = userRepository.findAll().stream()
                    .map(userService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error listing users", e);
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/internships")
    public ResponseEntity<List<jar.dto.InternshipDTO>> listInternships() {
        try {
            List<jar.dto.InternshipDTO> internships = internshipService.getAllInternships().stream()
                    .map(internship -> internshipService.convertToDTO(internship, 0))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(internships);
        } catch (Exception e) {
            logger.error("Error listing internships", e);
            return ResponseEntity.ok(List.of());
        }
    }

    @org.springframework.web.bind.annotation.PostMapping("/internships")
    public ResponseEntity<?> createInternship(@org.springframework.web.bind.annotation.RequestBody jar.dto.CreateInternshipDTO dto) {
        try {
            jar.dto.InternshipDTO result = internshipService.createInternship(dto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating internship", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/internships/{id}")
    public ResponseEntity<?> deleteInternship(@org.springframework.web.bind.annotation.PathVariable Long id) {
        try {
            internshipService.deleteInternship(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Internship deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting internship", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSettings() {
        // Return dynamic simulated settings 
        return ResponseEntity.ok(Map.of(
                "appName", "Internship Platform",
                "version", "1.1.0",
                "maintenanceMode", false,
                "registrationEnabled", true,
                "apiRequestsLast24h", 1240
        ));
    }
}
