package proje.cinepick.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_blacklists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "excluded_genres", columnDefinition = "text[]")
    private String[] excludedGenres;

    @Column(name = "excluded_directors", columnDefinition = "text[]")
    private String[] excludedDirectors;
}
