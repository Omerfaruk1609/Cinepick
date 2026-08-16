package proje.cinepick.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import proje.cinepick.entity.Movie;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTmdbId(Long tmdbId);

    @Query("SELECT m.tmdbId FROM Movie m WHERE m.tmdbId IS NOT NULL")
    Set<Long> findAllTmdbIds();

    @Query(value = """
        SELECT m.* FROM movies m 
        ORDER BY 
            ((COALESCE(m.vote_count, 0) * 1.0 / (COALESCE(m.vote_count, 0) + 300)) * COALESCE(m.vote_average, 0) + 
             (300.0 / (COALESCE(m.vote_count, 0) + 300)) * 6.5) DESC,
            m.vote_count DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Movie> findPopularMovies(@Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM movies WHERE title ~ '\\([0-9]+\\)$'", nativeQuery = true)
    long countNumberedTitles();

    default List<Movie> findTop10ByOrderByVoteAverageDesc() {
        return findPopularMovies(10);
    }

    @Query(value = """
        SELECT m.*, 
               (1 - (m.embedding <=> CAST(:userVector AS vector))) AS vector_similarity,
               (CASE WHEN CAST(m.genres AS text[]) && CAST(:favoriteGenres AS text[]) THEN 0.15 ELSE 0 END) AS genre_boost,
               ((1 - (m.embedding <=> CAST(:userVector AS vector))) + 
                (CASE WHEN CAST(m.genres AS text[]) && CAST(:favoriteGenres AS text[]) THEN 0.15 ELSE 0 END)) AS final_score
        FROM movies m
        WHERE m.id NOT IN (
            SELECT umi.movie_id 
            FROM user_movie_interactions umi 
            WHERE umi.user_id = :userId
        )
        AND m.embedding IS NOT NULL
        ORDER BY final_score DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Movie> findPersonalizedRecommendations(
            @Param("userId") Long userId,
            @Param("userVector") String userVector,
            @Param("favoriteGenres") String[] favoriteGenres,
            @Param("limit") int limit
    );

    @Query(value = """
        SELECT m.*, 
               (1 - (m.embedding <=> CAST(:userVector AS vector))) AS vector_similarity,
               (CASE WHEN CAST(m.genres AS text[]) && CAST(:favoriteGenres AS text[]) THEN 0.15 ELSE 0 END) AS genre_boost,
               ((1 - (m.embedding <=> CAST(:userVector AS vector))) + 
                (CASE WHEN CAST(m.genres AS text[]) && CAST(:favoriteGenres AS text[]) THEN 0.15 ELSE 0 END)) AS final_score
        FROM movies m
        WHERE m.id NOT IN (
            SELECT umi.movie_id FROM user_movie_interactions umi WHERE umi.user_id = :userId
        )
        -- KESİN ELEME (KARA LİSTE FİLTRELERİ)
        AND (CAST(:excludedGenres AS text[]) IS NULL OR NOT (CAST(m.genres AS text[]) && CAST(:excludedGenres AS text[])))
        AND (CAST(:excludedDirectors AS text[]) IS NULL OR NOT (m.director = ANY(CAST(:excludedDirectors AS text[]))))
        AND m.embedding IS NOT NULL
        ORDER BY final_score DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Movie> findPersonalizedRecommendationsWithBlacklist(
            @Param("userId") Long userId,
            @Param("userVector") String userVector,
            @Param("favoriteGenres") String[] favoriteGenres,
            @Param("excludedGenres") String[] excludedGenres,
            @Param("excludedDirectors") String[] excludedDirectors,
            @Param("limit") int limit
    );

    @Query(value = """
        SELECT m.*,
               (CASE WHEN :userVector IS NOT NULL AND m.embedding IS NOT NULL 
                     THEN (1 - (m.embedding <=> CAST(:userVector AS vector))) 
                     ELSE (((COALESCE(m.vote_count, 0) * 1.0 / (COALESCE(m.vote_count, 0) + 200)) * COALESCE(m.vote_average, 0) + 
                            (200.0 / (COALESCE(m.vote_count, 0) + 200)) * 6.5) / 10.0) END) AS score
        FROM movies m
        WHERE (CAST(:genres AS text[]) IS NULL OR CARDINALITY(CAST(:genres AS text[])) = 0 OR CAST(m.genres AS text[]) && CAST(:genres AS text[]))
          AND (:originalLanguage IS NULL OR :originalLanguage = '' OR :originalLanguage = 'all' OR 
               (:originalLanguage = 'tr' AND LOWER(m.original_language) = 'tr') OR
               (:originalLanguage = 'foreign' AND LOWER(m.original_language) != 'tr') OR
               (LOWER(m.original_language) = LOWER(:originalLanguage)))
          AND (:minYear IS NULL OR m.release_year >= :minYear)
          AND (:maxYear IS NULL OR m.release_year <= :maxYear)
          AND (:minRating IS NULL OR m.vote_average >= :minRating)
          AND (:maxRuntime IS NULL OR m.runtime <= :maxRuntime)
          AND (:platform IS NULL OR :platform = '' OR :platform = 'all' OR m.streaming_platforms ILIKE CONCAT('%', :platform, '%'))
        ORDER BY score DESC, m.vote_count DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Movie> filterMovies(
            @Param("userVector") String userVector,
            @Param("genres") String[] genres,
            @Param("originalLanguage") String originalLanguage,
            @Param("minYear") Integer minYear,
            @Param("maxYear") Integer maxYear,
            @Param("minRating") Double minRating,
            @Param("maxRuntime") Integer maxRuntime,
            @Param("platform") String platform,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
        SELECT m.*, 
               (1 - (m.embedding <=> CAST(:groupVector AS vector))) AS vector_similarity
        FROM movies m
        WHERE m.id NOT IN (
            SELECT umi.movie_id FROM user_movie_interactions umi WHERE umi.user_id = :user1Id
            UNION
            SELECT umi.movie_id FROM user_movie_interactions umi WHERE umi.user_id = :user2Id
        )
        AND m.embedding IS NOT NULL
        ORDER BY vector_similarity DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Movie> findGroupRecommendations(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id,
            @Param("groupVector") String groupVector,
            @Param("limit") int limit
    );

    @Query(value = "SELECT * FROM movies WHERE embedding IS NULL", nativeQuery = true)
    List<Movie> findUnindexedMovies(Pageable pageable);
}
