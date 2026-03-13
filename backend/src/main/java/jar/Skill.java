package jar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a skill tag that can be associated with users and internships.
 */
@Entity
@Table(name = "skills", uniqueConstraints = { @UniqueConstraint(columnNames = "name") },
       indexes = { @Index(name = "idx_skills_name", columnList = "name") })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Skill name is required")
    @Column(nullable = false, unique = true)
    private String name;

    // Owning side for User<->Skill
    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<User> users = new HashSet<>();

    // Owning side for Internship<->Skill
    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "internship_skills",
            joinColumns = @JoinColumn(name = "skill_id"),
            inverseJoinColumns = @JoinColumn(name = "internship_id"),
            indexes = {
                    @Index(name = "idx_internship_skills_skill", columnList = "skill_id"),
                    @Index(name = "idx_internship_skills_internship", columnList = "internship_id")
            }
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<Internship> internships = new HashSet<>();

}
