package jar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InternshipService {

    private static final Logger logger = LoggerFactory.getLogger(InternshipService.class);

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String JSEARCH_BASE_URL = "https://jsearch.p.rapidapi.com/search";
    private static final String JSEARCH_HOST = "jsearch.p.rapidapi.com";
    
    // IMPORTANT: Replace with your actual RapidAPI key
    private static final String JSEARCH_API_KEY = "YOUR_RAPIDAPI_KEY_HERE";

    /**
     * Fetch internships from JSearch API and save to database
     */
    public List<Internship> fetchAndSaveInternships(String query, int pages) {
        List<Internship> savedInternships = new ArrayList<>();
        
        try {
            for (int page = 1; page <= pages; page++) {
                List<Internship> pageInternships = fetchFromJSearchAPI(query, page);
                for (Internship internship : pageInternships) {
                    if (!internshipRepository.existsByExternalLink(internship.getExternalLink())) {
                        Internship saved = internshipRepository.save(internship);
                        savedInternships.add(saved);
                        logger.info("Saved internship: {}", saved.getTitle());
                    } else {
                        logger.debug("Internship already exists: {}", internship.getExternalLink());
                    }
                }
            }
            logger.info("Fetched and saved {} new internships", savedInternships.size());
        } catch (Exception e) {
            logger.error("Error fetching internships from external API", e);
        }
        
        return savedInternships;
    }

    /**
     * Fetch from JSearch API (RapidAPI)
     */
    private List<Internship> fetchFromJSearchAPI(String query, int page) {
        List<Internship> internships = new ArrayList<>();
        
        try {
            String url = String.format("%s?query=%s&page=%d&num_pages=1", 
                    JSEARCH_BASE_URL, query, page);

            Map<String, String> headers = new HashMap<>();
            headers.put("X-RapidAPI-Key", JSEARCH_API_KEY);
            headers.put("X-RapidAPI-Host", JSEARCH_HOST);

            // Note: RestTemplate doesn't directly support custom headers in GET
            // You may need to use interceptors or create a custom RestTemplate bean
            // For now, using a simple approach with string concatenation
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                internships = parseJSearchResponse(response);
            }
        } catch (Exception e) {
            logger.error("Error fetching from JSearch API", e);
        }
        
        return internships;
    }

    /**
     * Parse JSearch API response
     */
    private List<Internship> parseJSearchResponse(String jsonResponse) {
        List<Internship> internships = new ArrayList<>();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode dataArray = root.path("data");

            if (dataArray.isArray()) {
                for (JsonNode jobNode : dataArray) {
                    Internship internship = mapJSearchJobToInternship(jobNode);
                    if (internship != null) {
                        internships.add(internship);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing JSearch response", e);
        }
        
        return internships;
    }

    /**
     * Map JSearch job object to Internship entity
     */
    private Internship mapJSearchJobToInternship(JsonNode jobNode) {
        try {
            String jobId = jobNode.path("job_id").asText();
            if (jobId.isEmpty()) {
                return null;
            }

            String title = jobNode.path("job_title").asText("Not specified");
            String company = jobNode.path("employer_name").asText("Not specified");
            String jobDescription = jobNode.path("job_description").asText("");
                    
            // Extract skills from job description (simple extraction)
            extractSkills(jobDescription);
                    
            Internship internship = Internship.builder()
                    .title(title)
                    .company(company)
                    .source("JSearch")
                    .externalLink(jobId)
                    .build();
            
            return internship;
        } catch (Exception e) {
            logger.error("Error mapping job to internship", e);
            return null;
        }
    }

    /**
     * Extract skills from job description (simple regex-based extraction)
     */
    @Autowired
    private SkillRepository skillRepository;

    /**
     * Create internship using provided DTO logic
     */
    public jar.dto.InternshipDTO createInternship(jar.dto.CreateInternshipDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
            dto.getCompany() == null || dto.getCompany().trim().isEmpty()) {
            throw new IllegalArgumentException("Title and Company are required.");
        }

        Internship internship = new Internship();
        internship.setTitle(dto.getTitle());
        internship.setCompany(dto.getCompany());
        internship.setLocation(dto.getLocation());
        internship.setDescription(dto.getDescription());
        internship.setDuration(dto.getDuration());
        internship.setStipend(dto.getStipend());
        internship.setDeadline(dto.getDeadline());
        internship.setExternalLink(dto.getExternalLink());
        internship.setSource("admin");

        Internship saved = internshipRepository.save(internship);

        String skillsStr = dto.getSkills();
        if (skillsStr != null && !skillsStr.trim().isEmpty()) {
            String[] skillArray = skillsStr.split(",");
            for (String sk : skillArray) {
                String trimmed = sk.trim();
                if (!trimmed.isEmpty()) {
                    Skill skill = skillRepository.findByNameIgnoreCase(trimmed).orElseGet(() -> {
                        Skill newSkill = new Skill();
                        newSkill.setName(trimmed);
                        return skillRepository.save(newSkill);
                    });
                    if (skill.getInternships() == null) {
                        skill.setInternships(new java.util.HashSet<>());
                    }
                    if (saved.getSkills() == null) {
                        saved.setSkills(new java.util.HashSet<>());
                    }
                    skill.getInternships().add(saved);
                    saved.getSkills().add(skill);
                    skillRepository.save(skill);
                }
            }
        }

        return convertToDTO(saved, 0);
    }

    /**
     * Extract skills from job description (simple regex-based extraction)
     */
    private String extractSkills(String description) {
        if (description == null || description.isEmpty()) {
            return "Not specified";
        }

        String[] commonSkills = {
                "Java", "Python", "JavaScript", "TypeScript", "React", "Angular", "Vue",
                "Spring", "Node.js", "SQL", "MongoDB", "AWS", "Docker", "Kubernetes",
                "Git", "REST API", "GraphQL", "C++", "Go", "Rust", ".NET", "Azure",
                "Machine Learning", "Data Science", "DevOps", "CI/CD", "Linux", "HTML", "CSS"
        };

        Set<String> foundSkills = new HashSet<>();
        String lowerDesc = description.toLowerCase();

        for (String skill : commonSkills) {
            if (lowerDesc.contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }

        if (foundSkills.isEmpty()) {
            return "Not specified";
        }

        return String.join(", ", foundSkills);
    }

    /**
     * Get all internships from database
     */
    public List<Internship> getAllInternships() {
        try {
            List<Internship> result = internshipRepository.findAllWithSkills();
            return result != null ? result : List.of();
        } catch (Exception e) {
            logger.error("Error fetching all internships", e);
            return List.of();
        }
    }

    /**
     * Get internships by company
     */
    public List<Internship> getInternshipsByCompany(String company) {
        try {
            return internshipRepository.findByCompany(company).stream()
                    .peek(i -> i.getSkills().size())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching internships by company", e);
            return List.of();
        }
    }


    /**
     * Search internships by title
     */
    public List<Internship> searchInternships(String title) {
        try {
            return internshipRepository.findByTitleContainingIgnoreCase(title).stream()
                    .peek(i -> i.getSkills().size())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching internships", e);
            return List.of();
        }
    }

    /**
     * Get internships by source
     */
    public List<Internship> getInternshipsBySource(String source) {
        try {
            return internshipRepository.findBySource(source).stream()
                    .peek(i -> i.getSkills().size())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching internships by source", e);
            return List.of();
        }
    }

    /**
     * Get internship by ID (with skills loaded to avoid LazyInitializationException)
     */
    public Optional<Internship> getInternshipById(Long id) {
        return internshipRepository.findByIdWithSkills(id);
    }

    /**
     * Match internships for a user based on their skills using weighted scoring.
     * 
     * Scoring Algorithm:
     * - For each required skill in the internship:
     *   - Exact match with user skill: +2
     *   - Partial match (skill name contains user skill keyword): +1
     *   - Title contains required skill: +1
     * - For each user skill:
     *   - Title contains user skill (not already matched): +1
     * 
     * Sorting: By score (descending), then by creation date (most recent first)
     * 
     * If user has no skills, return all internships sorted by creation date.
     */
    public List<InternshipMatchResult> matchInternshipsForUser(Long userId) {
        try {
            logger.info("Matching internships for user: {}", userId);

            // Fetch user by ID with skills to avoid lazy initialization issues
            User user = userRepository.findByIdWithSkills(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get user's skills (safe default to empty set)
            Set<Skill> userSkills = user.getSkills() != null ? user.getSkills() : Set.of();
            logger.debug("User {} has {} skills", userId, userSkills.size());

            // Fetch all internships with skills
            List<Internship> allInternships = internshipRepository.findAllWithSkills();
            if (allInternships == null) {
                allInternships = List.of();
            }
            logger.debug("Total internships in database: {}", allInternships.size());

            // If user has no skills, return an empty list
            if (userSkills == null || userSkills.isEmpty()) {
                logger.info("User has no skills, returning empty matching list.");
                return List.of();
            }

            // Match internships using weighted scoring
            return allInternships.stream()
                    .map(internship -> {
                        int score = calculateMatchScore(internship, userSkills);
                        return new InternshipMatchResult(internship, score);
                    })
                    .filter(result -> result != null && result.getScore() > 0)
                    .sorted(Comparator
                            .comparingInt(InternshipMatchResult::getScore).reversed()
                            .thenComparing(InternshipMatchResult::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error matching internships for user {}", userId, e);
            return List.of();
        }
    }

    /**
     * Calculate weighted relevance score for an internship based on user's skills.
     * 
     * Scoring:
     * - Exact skill match: +2
     * - Partial match (skill name contains user skill): +1
     * - Title contains required skill: +1
     * - Title contains user skill (not already matched): +1
     */
    private int calculateMatchScore(Internship internship, Set<Skill> userSkills) {
        try {
            int score = 0;

            if (internship == null || userSkills == null || userSkills.isEmpty()) {
                return 0;
            }

            Set<String> userSkillNames = userSkills.stream()
                    .map(s -> s.getName())
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            String titleLower = internship.getTitle() != null ? internship.getTitle().toLowerCase() : "";
            Set<Skill> requiredSkills = internship.getSkills() != null ? internship.getSkills() : java.util.Collections.emptySet();

            for (Skill requiredSkill : requiredSkills) {
                if (requiredSkill == null || requiredSkill.getName() == null) {
                    continue;
                }

                String skillNameLower = requiredSkill.getName().toLowerCase();

                if (userSkillNames.contains(skillNameLower)) {
                    score += 2;
                    if (!titleLower.isEmpty() && titleLower.contains(skillNameLower)) {
                        score += 1;
                    }
                } else {
                    for (String userSkill : userSkillNames) {
                        if (skillNameLower.contains(userSkill)) {
                            score += 1;
                            break;
                        }
                    }
                    if (!titleLower.isEmpty() && titleLower.contains(skillNameLower)) {
                        score += 1;
                    }
                }
            }

            for (String userSkill : userSkillNames) {
                if (!userSkill.isEmpty() && titleLower.contains(userSkill)) {
                    boolean isRequiredSkill = requiredSkills.stream()
                            .filter(Objects::nonNull)
                            .map(s -> s.getName())
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .anyMatch(userSkill::equals);
                    if (!isRequiredSkill) {
                        score += 1;
                    }
                }
            }

            return score;
        } catch (Exception e) {
            logger.error("Error calculating match score for internship {}", internship != null ? internship.getId() : null, e);
            return 0;
        }
    }

    /**
     * Convert Internship to InternshipDTO with relevance score.
     */
    public jar.dto.InternshipDTO convertToDTO(Internship internship, int score) {
        if (internship == null) {
            return jar.dto.InternshipDTO.builder()
                    .id(null)
                    .title("Unknown")
                    .company("Unknown")
                    .source("Unknown")
                    .externalLink(null)
                    .location(null)
                    .description(null)
                    .duration(null)
                    .stipend(null)
                    .deadline(null)
                    .createdAt(null)
                    .skills(List.of())
                    .score(0)
                    .build();
        }

        java.util.Set<Skill> effectiveSkills = internship.getSkills() != null ? internship.getSkills() : java.util.Collections.emptySet();
        java.util.List<String> skillNames = effectiveSkills.stream()
                .filter(java.util.Objects::nonNull)
                .map(s -> s.getName())
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        return jar.dto.InternshipDTO.builder()
                .id(internship.getId())
                .title(internship.getTitle() != null ? internship.getTitle() : "Unknown")
                .company(internship.getCompany() != null ? internship.getCompany() : "Unknown")
                .source(internship.getSource())
                .externalLink(internship.getExternalLink())
                .location(internship.getLocation())
                .description(internship.getDescription())
                .duration(internship.getDuration())
                .stipend(internship.getStipend())
                .deadline(internship.getDeadline())
                .createdAt(internship.getCreatedAt())
                .skills(skillNames)
                .score(score)
                .build();
    }

    /**
     * Delete old internships to keep database clean
     */
    public void deleteInternshipsOlderThanDays(int days) {
        List<Internship> allInternships = internshipRepository.findAll();
        int deletedCount = 0;

        for (Internship internship : allInternships) {
            if (internship.getCreatedAt() != null) {
                long daysDifference = java.time.temporal.ChronoUnit.DAYS.between(
                        internship.getCreatedAt().toLocalDate(),
                        java.time.LocalDate.now()
                );
                if (daysDifference > days) {
                    internshipRepository.delete(internship);
                    deletedCount++;
                }
            }
        }

        logger.info("Deleted {} old internships", deletedCount);
    }

    @Autowired
    private SavedInternshipRepository savedInternshipRepository;

    @org.springframework.transaction.annotation.Transactional
    public void deleteInternship(Long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with ID: " + id));

        // 1. Remove associations with skills (Many-to-Many)
        for (Skill skill : internship.getSkills()) {
            skill.getInternships().remove(internship);
            skillRepository.save(skill);
        }
        internship.getSkills().clear();

        // 2. Delete dependent SavedInternship records
        savedInternshipRepository.deleteByInternshipId(id);

        // 3. Delete the actual internship
        internshipRepository.delete(internship);
        logger.info("Deleted internship with ID {}", id);
    }
}
