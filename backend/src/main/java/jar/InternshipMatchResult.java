package jar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Result of internship matching for a user.
 * Holds the internship with its weighted relevance score for sorting and filtering.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipMatchResult {
    private Internship internship;
    
    /**
     * Weighted relevance score calculated as:
     * - Exact skill match: +2
     * - Partial skill match (keyword): +1
     * - Title contains required skill: +1
     * - Title contains user skill: +1
     */
    private int score;

    /**
     * Convenience getter for internship creation date for sorting.
     */
    public LocalDateTime getCreatedAt() {
        return internship.getCreatedAt();
    }
}
