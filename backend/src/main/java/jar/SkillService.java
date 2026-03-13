package jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Skill operations.
 * Handles business logic for skill creation, retrieval, and management.
 */
@Service
public class SkillService {

    private static final Logger logger = LoggerFactory.getLogger(SkillService.class);

    private final SkillRepository skillRepository;

    /**
     * Constructor injection.
     */
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Create a new skill if it doesn't already exist.
     * Returns existing skill if name already exists (case-insensitive).
     */
    public Skill createSkill(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Skill name is required");
        }

        Optional<Skill> existingSkill = skillRepository.findByNameIgnoreCase(name.trim());
        if (existingSkill.isPresent()) {
            logger.info("Skill already exists: {}", name);
            return existingSkill.get();
        }

        Skill newSkill = Skill.builder()
                .name(name.trim())
                .build();

        Skill savedSkill = skillRepository.save(newSkill);
        logger.info("New skill created: {}", name);
        return savedSkill;
    }

    /**
     * Get all skills.
     */
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    /**
     * Get skill by ID.
     */
    public Optional<Skill> getSkillById(Long skillId) {
        return skillRepository.findById(skillId);
    }

    /**
     * Get skill by name (case-insensitive).
     */
    public Optional<Skill> getSkillByName(String name) {
        return skillRepository.findByNameIgnoreCase(name);
    }

    /**
     * Delete skill by ID.
     */
    public void deleteSkill(Long skillId) {
        skillRepository.deleteById(skillId);
        logger.info("Skill deleted: {}", skillId);
    }

    /**
     * Check if skill exists by name.
     */
    public boolean skillExists(String name) {
        return skillRepository.existsByNameIgnoreCase(name);
    }
}
