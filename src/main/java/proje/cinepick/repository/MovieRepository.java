package proje.cinepick.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import proje.cinepick.entity.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTmdbId(Long tmdbId);

    @Query(value = "SELECT m.* FROM movies m LEFT JOIN movie_embeddings e ON m.tmdb_id = e.movie_tmdb_id WHERE e.id IS NULL", nativeQuery = true)
    List<Movie> findUnindexedMovies(Pageable pageable);
}
