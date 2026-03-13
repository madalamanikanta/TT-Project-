package jar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Skill entity.
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    /**
     * Find skill by name (case-insensitive)
     */
    Optional<Skill> findByNameIgnoreCase(String name);

    /**
     * Check if skill exists by name
     */
    boolean existsByNameIgnoreCase(String name);
}
