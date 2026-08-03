package proje.cinepick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proje.cinepick.entity.UserMovieInteraction;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMovieInteractionRepository extends JpaRepository<UserMovieInteraction, Long> {

    Optional<UserMovieInteraction> findByUserIdAndMovieId(Long userId, Long movieId);

    Optional<UserMovieInteraction> findByUserIdAndMovieTmdbId(Long userId, Long tmdbId);

    List<UserMovieInteraction> findByUserIdAndIsFavoriteTrue(Long userId);

    List<UserMovieInteraction> findByUserIdAndInWatchlistTrue(Long userId);

    List<UserMovieInteraction> findTop20ByUserIdOrderByUpdatedAtDesc(Long userId);
}
