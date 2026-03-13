package jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for managing user skills.
 * Handles adding, retrieving, and removing skills from users.
 */
@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
@RequestMapping("/api/users/{userId}/skills")
public class UserSkillController {

    private static final Logger logger = LoggerFactory.getLogger(UserSkillController.class);

    private final UserService userService;
    private final SkillService skillService;

    /**
     * Constructor injection.
     */
    public UserSkillController(UserService userService, SkillService skillService) {
        this.userService = userService;
        this.skillService = skillService;
    }

    /**
     * POST /api/users/{userId}/skills
     * Add skills to user.
     * Body: { "skillIds": [1, 2, 3] } or { "skillNames": ["Java", "Python"] }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addSkillsToUser(
            @PathVariable Long userId,
            @RequestBody AddSkillRequest request) {
        try {
            logger.info("Adding skills to user: {}", userId);

            // Handle adding by skill IDs
            if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
                for (Long skillId : request.getSkillIds()) {
                    Skill skill = skillService.getSkillById(skillId)
                            .orElseThrow(() -> new RuntimeException("Skill not found: " + skillId));
                    userService.addSkillToUser(userId, skill);
                }
            }

            // Handle adding by skill names
            if (request.getSkillNames() != null && !request.getSkillNames().isEmpty()) {
                for (String skillName : request.getSkillNames()) {
                    Skill skill = skillService.getSkillByName(skillName)
                            .orElseGet(() -> skillService.createSkill(skillName));
                    userService.addSkillToUser(userId, skill);
                }
            }

            Set<Skill> userSkills = userService.getUserSkills(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Skills added successfully");
            // keep old key for backward compatibility
            response.put("skills", userSkills);
            // standardize on "data" like other controllers
            response.put("data", userSkills);
            response.put("skillCount", userSkills.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error adding skills to user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Error adding skills: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error adding skills to user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * GET /api/users/{userId}/skills
     * Get user skills.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserSkills(@PathVariable Long userId) {
        try {
            logger.info("Fetching skills for user: {}", userId);

            Set<Skill> skills = userService.getUserSkills(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("skillCount", skills.size());
            response.put("skills", skills);
            response.put("data", skills);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error fetching skills for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("User not found: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching skills for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/users/{userId}/skills/{skillId}
     * Remove skill from user.
     */
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Map<String, Object>> removeSkillFromUser(
            @PathVariable Long userId,
            @PathVariable Long skillId) {
        try {
            logger.info("Removing skill {} from user: {}", skillId, userId);

            Skill skill = skillService.getSkillById(skillId)
                    .orElseThrow(() -> new RuntimeException("Skill not found: " + skillId));

            userService.removeSkillFromUser(userId, skill);

            Set<Skill> userSkills = userService.getUserSkills(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Skill removed successfully");
            response.put("skills", userSkills);
            response.put("data", userSkills);
            response.put("skillCount", userSkills.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error removing skill {} from user: {}", skillId, userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Error removing skill: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error removing skill {} from user: {}", skillId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * Helper method to create error response.
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }

    /**
     * Request DTO for adding skills.
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    public static class AddSkillRequest {
        private java.util.List<Long> skillIds;
        private java.util.List<String> skillNames;
    }
}
