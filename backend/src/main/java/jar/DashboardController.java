package jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Aggregated dashboard endpoint used by the frontend.
 *
 * Returns current user skills along with matched internships so the UI only
 * needs to hit a single URL.  The controller is secured via JWT and pulls the
 * user id from the Authorization header.
 */
@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
@RequestMapping("/api")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final InternshipService internshipService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public DashboardController(InternshipService internshipService,
                               UserService userService,
                               JwtUtil jwtUtil) {
        this.internshipService = internshipService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            logger.info("Dashboard requested for user {}", userId);

            // fetch skills
            Set<Skill> skillSet = userService.getUserSkills(userId);

            // fetch match results and convert to DTOs
            List<InternshipMatchResult> matchResults = internshipService.matchInternshipsForUser(userId);
            List<jar.dto.InternshipDTO> internships = matchResults.stream()
                    .map(r -> internshipService.convertToDTO(r.getInternship(), r.getScore()))
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("skillCount", skillSet.size());
            response.put("skills", skillSet);
            response.put("matches", internships);
            response.put("matchCount", internships.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Dashboard error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(errorMap(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected dashboard error", e);
            return ResponseEntity.status(500).body(errorMap("Internal error: " + e.getMessage()));
        }
    }

    private Long extractUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            return jwtUtil.extractUserId(token);
        }
        throw new RuntimeException("Missing Authorization header");
    }

    private Map<String, Object> errorMap(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("error", msg);
        return map;
    }
}
