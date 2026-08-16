package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proje.cinepick.dto.InteractionRequest;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.dto.OnboardingRatingRequest;
import proje.cinepick.entity.Movie;
import proje.cinepick.entity.User;
import proje.cinepick.entity.UserMovieInteraction;
import proje.cinepick.repository.MovieRepository;
import proje.cinepick.repository.UserMovieInteractionRepository;
import proje.cinepick.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserInteractionService {

    private final UserMovieInteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CachedUserVectorService cachedUserVectorService;

    @Transactional
    public void updateInteraction(String username, InteractionRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username)));

        Long movieId = request.getMovieId();
        Movie movie = movieRepository.findById(movieId)
                .orElseGet(() -> movieRepository.findByTmdbId(movieId)
                        .orElseGet(() -> movieRepository.save(
                                Movie.builder()
                                        .tmdbId(movieId)
                                        .title("Movie #" + movieId)
                                        .overview("")
                                        .build()
                        )));

        UserMovieInteraction interaction = interactionRepository
                .findByUserIdAndMovieId(user.getId(), movie.getId())
                .orElseGet(() -> UserMovieInteraction.builder()
                        .user(user)
                        .movie(movie)
                        .build());

        if (request.getIsFavorite() != null) {
            interaction.setFavorite(request.getIsFavorite());
        }
        if (request.getInWatchlist() != null) {
            interaction.setInWatchlist(request.getInWatchlist());
        }
        if (request.getRating() != null) {
            interaction.setRating(request.getRating());
        }

        interactionRepository.save(interaction);
        cachedUserVectorService.evictUserVectorCache(user.getId());
    }

    @Transactional
    public void saveOnboardingRatings(String username, List<OnboardingRatingRequest> ratings) {
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username)));

        if (ratings != null) {
            for (OnboardingRatingRequest req : ratings) {
                if (req.movieId() == null || "SKIP".equalsIgnoreCase(req.preference())) {
                    continue;
                }

                Long movieId = req.movieId();
                Movie movie = movieRepository.findById(movieId)
                        .orElseGet(() -> movieRepository.findByTmdbId(movieId)
                                .orElseGet(() -> movieRepository.save(
                                        Movie.builder()
                                                .tmdbId(movieId)
                                                .title("Movie #" + movieId)
                                                .overview("")
                                                .build()
                                )));

                boolean isLike = "LIKE".equalsIgnoreCase(req.preference());

                UserMovieInteraction interaction = interactionRepository
                        .findByUserIdAndMovieId(user.getId(), movie.getId())
                        .orElseGet(() -> UserMovieInteraction.builder()
                                .user(user)
                                .movie(movie)
                                .build());

                interaction.setFavorite(isLike);
                interaction.setRating(isLike ? 5.0 : 1.0);
                interactionRepository.save(interaction);
            }
        }

        user.setHasCompletedOnboarding(true);
        userRepository.save(user);
        cachedUserVectorService.evictUserVectorCache(user.getId());
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getUserFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username)));

        return interactionRepository.findByUserIdAndIsFavoriteTrue(user.getId())
                .stream()
                .map(umi -> mapToMovieDto(umi.getMovie()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getUserWatchlist(String username) {
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username)));

        return interactionRepository.findByUserIdAndInWatchlistTrue(user.getId())
                .stream()
                .map(umi -> mapToMovieDto(umi.getMovie()))
                .collect(Collectors.toList());
    }

    private MovieDto mapToMovieDto(Movie movie) {
        return MovieDto.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .posterPath(movie.getPosterPath())
                .releaseDate(movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null)
                .voteAverage(movie.getVoteAverage())
                .build();
    }
}
