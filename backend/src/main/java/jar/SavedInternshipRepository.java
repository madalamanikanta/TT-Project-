package jar;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SavedInternshipRepository extends JpaRepository<SavedInternship, Long> {
    List<SavedInternship> findByUserId(Long userId);
    Optional<SavedInternship> findByUserIdAndInternshipId(Long userId, Long internshipId);
    void deleteByUserIdAndInternshipId(Long userId, Long internshipId);
}
