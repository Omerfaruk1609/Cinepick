package proje.cinepick.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import proje.cinepick.entity.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTmdbId(Long tmdbId);

    List<Movie> findTop10ByOrderByVoteAverageDesc();

    @Query(value = """
        SELECT m.*, 
               (1 - (m.embedding <=> CAST(:userVector AS vector))) AS vector_similarity,
               (CASE WHEN m.genres && CAST(:favoriteGenres AS text[]) THEN 0.15 ELSE 0 END) AS genre_boost,
               ((1 - (m.embedding <=> CAST(:userVector AS vector))) + 
                (CASE WHEN m.genres && CAST(:favoriteGenres AS text[]) THEN 0.15 ELSE 0 END)) AS final_score
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
               (CASE WHEN m.genres && CAST(:favoriteGenres AS text[]) THEN 0.15 ELSE 0 END) AS genre_boost,
               ((1 - (m.embedding <=> CAST(:userVector AS vector))) + 
                (CASE WHEN m.genres && CAST(:favoriteGenres AS text[]) THEN 0.15 ELSE 0 END)) AS final_score
        FROM movies m
        WHERE m.id NOT IN (
            SELECT umi.movie_id FROM user_movie_interactions umi WHERE umi.user_id = :userId
        )
        -- KESİN ELEME (KARA LİSTE FİLTRELERİ)
        AND (CAST(:excludedGenres AS text[]) IS NULL OR NOT (m.genres && CAST(:excludedGenres AS text[])))
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

    @Query(value = "SELECT m.* FROM movies m LEFT JOIN movie_embeddings e ON m.tmdb_id = e.movie_tmdb_id WHERE e.id IS NULL", nativeQuery = true)
    List<Movie> findUnindexedMovies(Pageable pageable);
}
