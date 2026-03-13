package jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SavedInternshipService {
    private static final Logger logger = LoggerFactory.getLogger(SavedInternshipService.class);

    @Autowired
    private SavedInternshipRepository savedRepo;

    @Autowired
    private InternshipRepository internshipRepository;

    /**
     * Save an internship for the given user.
     * Will throw if the internship does not exist or is already saved.
     */
    public SavedInternship save(Long userId, Long internshipId) {
        logger.info("User {} saving internship {}", userId, internshipId);
        internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found: " + internshipId));

        if (savedRepo.findByUserIdAndInternshipId(userId, internshipId).isPresent()) {
            throw new RuntimeException("Internship already saved");
        }

        SavedInternship rec = SavedInternship.builder()
                .userId(userId)
                .internshipId(internshipId)
                .build();
        return savedRepo.save(rec);
    }

    /**
     * Return list of internships bookmarked by user.  Internships that have been
     * deleted from the system will be silently ignored.
     */
    public List<Internship> getSavedInternships(Long userId) {
        return savedRepo.findByUserId(userId).stream()
                .map(si -> internshipRepository.findById(si.getInternshipId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public boolean isSaved(Long userId, Long internshipId) {
        return savedRepo.findByUserIdAndInternshipId(userId, internshipId).isPresent();
    }

    public void delete(Long userId, Long internshipId) {
        logger.info("User {} removing saved internship {}", userId, internshipId);
        savedRepo.deleteByUserIdAndInternshipId(userId, internshipId);
    }
}
