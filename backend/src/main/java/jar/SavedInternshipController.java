package jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/saved-internships")
public class SavedInternshipController {

    private static final Logger logger = LoggerFactory.getLogger(SavedInternshipController.class);

    private final SavedInternshipService savedService;
    private final JwtUtil jwtUtil;

    public SavedInternshipController(SavedInternshipService savedService, JwtUtil jwtUtil) {
        this.savedService = savedService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Helper to read the user id from the Authorization header JWT.
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Unauthorized: missing token in Authorization header or cookie");
        }
        return jwtUtil.extractUserId(token);
    }

    /**
     * POST /api/saved-internships/{internshipId}
     * Save internship for the authenticated user.
     */
    @PostMapping("/{internshipId}")
    public ResponseEntity<?> saveInternship(
            @PathVariable Long internshipId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            savedService.save(userId, internshipId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Internship saved successfully");
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            logger.warn("Failed to save internship: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error saving internship", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error saving internship: " + e.getMessage()));
        }
    }

    /**
     * GET /api/saved-internships
     * Return all internships saved by authenticated user.
     */
    @GetMapping
    public ResponseEntity<?> getSaved(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<Internship> internships = savedService.getSavedInternships(userId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("count", internships.size());
            resp.put("data", internships);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            logger.error("Error fetching saved internships", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error fetching saved internships: " + e.getMessage()));
        }
    }

    /**
     * GET /api/saved-internships/{internshipId}
     * Check if a particular internship is saved by the user.
     * Returns { success: true, saved: true/false }
     */
    @GetMapping("/{internshipId}")
    public ResponseEntity<?> checkSaved(
            @PathVariable Long internshipId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            boolean saved = savedService.isSaved(userId, internshipId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("saved", saved);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            logger.error("Error checking saved internship status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error checking saved internship: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/saved-internships/{internshipId}
     * Remove a saved internship for the user.
     */
    @DeleteMapping("/{internshipId}")
    public ResponseEntity<?> deleteSaved(
            @PathVariable Long internshipId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            savedService.delete(userId, internshipId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Removed saved internship");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            logger.error("Error deleting saved internship", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error deleting saved internship: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
