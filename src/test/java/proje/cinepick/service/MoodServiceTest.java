package proje.cinepick.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proje.cinepick.dto.MoodRequest;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MoodServiceTest {

    @Mock
    private LocalEmbeddingService localEmbeddingService;

    @Mock
    private MovieRepository movieRepository;

    private MoodRecommendationService moodRecommendationService;

    @BeforeEach
    void setUp() {
        moodRecommendationService = new MoodRecommendationService(localEmbeddingService, movieRepository);
    }

    @Test
    void recommendByMood_PresetTag_GeneratesVectorAndAppliesGenreBoost() {
        float[] fakeMoodVector = new float[384];
        fakeMoodVector[0] = 1.0f;

        when(localEmbeddingService.generateEmbedding(anyString())).thenReturn(fakeMoodVector);

        Movie movie = Movie.builder()
                .id(1L)
                .title("Action Thriller")
                .genres(new String[]{"Action", "Thriller"})
                .voteAverage(8.5)
                .build();

        when(movieRepository.filterMovies(anyString(), any(), any(), any(), any(), any(), any(), any(), eq(20), eq(0)))
                .thenReturn(List.of(movie));

        MoodRequest request = MoodRequest.builder()
                .moodTag("energetic")
                .limit(20)
                .build();

        List<MovieDto> results = moodRecommendationService.recommendByMood(request);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTitle()).isEqualTo("Action Thriller");
    }

    @Test
    void recommendByMood_CustomText_TranslatesToVectorSearch() {
        float[] fakeMoodVector = new float[384];
        fakeMoodVector[5] = 1.0f;

        when(localEmbeddingService.generateEmbedding("melancholic rainy night drama")).thenReturn(fakeMoodVector);

        Movie movie = Movie.builder()
                .id(2L)
                .title("Emotional Story")
                .genres(new String[]{"Drama"})
                .voteAverage(8.0)
                .build();

        when(movieRepository.filterMovies(anyString(), any(), any(), any(), any(), any(), any(), any(), eq(10), eq(0)))
                .thenReturn(List.of(movie));

        MoodRequest request = MoodRequest.builder()
                .moodText("melancholic rainy night drama")
                .limit(10)
                .build();

        List<MovieDto> results = moodRecommendationService.recommendByMood(request);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Emotional Story");
    }
}
