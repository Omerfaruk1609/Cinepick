package proje.cinepick.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "vote_average")
    private Double voteAverage;

    @Convert(converter = proje.cinepick.entity.converter.VectorConverter.class)
    @org.hibernate.annotations.ColumnTransformer(read = "embedding::text", write = "CAST(? AS vector)")
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    private float[] embedding;


    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "genres", columnDefinition = "text[]")
    private String[] genres;


    @Column(name = "director")
    private String director;

    @Column(name = "vote_count")
    private Long voteCount;
}
