package jar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for skill response.
 * Safe for API responses without bi-directional relationships.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillDTO {
    private Long id;
    private String name;
}
