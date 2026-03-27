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
            Long userId = null;
            try {
                userId = extractUserId(request);
            } catch (RuntimeException e) {
                logger.warn("Missing or invalid auth token for dashboard request: {}", e.getMessage());
            }
            logger.info("Dashboard requested for user {}", userId == null ? "unknown" : userId);

            if (userId == null) {
                Map<String, Object> safeResponse = new HashMap<>();
                safeResponse.put("success", false);
                safeResponse.put("skillCount", 0);
                safeResponse.put("skills", List.of());
                safeResponse.put("matches", List.of());
                safeResponse.put("matchCount", 0);
                safeResponse.put("message", "User not authenticated");
                return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body(safeResponse);
            }

            Set<Skill> skillSet = userService.getUserSkills(userId);
            if (skillSet == null) {
                skillSet = java.util.Collections.emptySet();
            }

            List<jar.dto.SkillDTO> skillDTOs = skillSet.stream()
                    .filter(java.util.Objects::nonNull)
                    .map(skill -> jar.dto.SkillDTO.builder()
                            .id(skill.getId())
                            .name(skill.getName())
                            .build())
                    .collect(Collectors.toList());

            List<InternshipMatchResult> matchResults = internshipService.matchInternshipsForUser(userId);
            if (matchResults == null) {
                matchResults = List.of();
            }

            List<jar.dto.InternshipDTO> internships = matchResults.stream()
                    .filter(java.util.Objects::nonNull)
                    .map(r -> {
                        if (r == null || r.getInternship() == null) {
                            return null;
                        }
                        return internshipService.convertToDTO(r.getInternship(), r.getScore());
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("skillCount", skillDTOs.size());
            response.put("skills", skillDTOs);
            response.put("matches", internships);
            response.put("matchCount", internships.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected dashboard error", e);
            Map<String, Object> safeResponse = new HashMap<>();
            safeResponse.put("success", false);
            safeResponse.put("skillCount", 0);
            safeResponse.put("skills", List.of());
            safeResponse.put("matches", List.of());
            safeResponse.put("matchCount", 0);
            safeResponse.put("error", "Internal error: " + e.getMessage());
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(safeResponse);
        }
    }

    private Long extractUserId(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Missing token in Authorization header or cookie");
        }
        return jwtUtil.extractUserId(token);
    }

    private Map<String, Object> errorMap(String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("error", msg);
        return map;
    }
}
