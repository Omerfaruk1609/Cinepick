package proje.cinepick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import proje.cinepick.entity.ListStatus;
import proje.cinepick.entity.UserMovieInteraction;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMovieInteractionRepository extends JpaRepository<UserMovieInteraction, Long> {
    Optional<UserMovieInteraction> findByUserIdAndMovieTmdbId(Long userId, Long tmdbId);

    List<UserMovieInteraction> findByUserIdAndListStatus(Long userId, ListStatus listStatus);

    List<UserMovieInteraction> findByUserIdAndUserRatingIsNotNull(Long userId);

    List<UserMovieInteraction> findByUserId(Long userId);

    @Query("SELECT AVG(umi.userRating) FROM UserMovieInteraction umi WHERE umi.movie.tmdbId = :tmdbId AND umi.userRating IS NOT NULL")
    Optional<Double> findAverageRatingByMovieTmdbId(@Param("tmdbId") Long tmdbId);
}
