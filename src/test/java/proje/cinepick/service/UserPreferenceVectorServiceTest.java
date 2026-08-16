package proje.cinepick.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proje.cinepick.entity.Movie;
import proje.cinepick.entity.UserMovieInteraction;
import proje.cinepick.repository.UserMovieInteractionRepository;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.util.VectorMathUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserPreferenceVectorServiceTest {

    @Mock
    private UserMovieInteractionRepository interactionRepository;

    @Mock
    private UserRepository userRepository;

    private UserPreferenceVectorService service;

    @BeforeEach
    void setUp() {
        service = new UserPreferenceVectorService(interactionRepository, userRepository);
    }

    @Test
    void calculateUserVector_ColdStart_ReturnsNull() {
        when(interactionRepository.findTop20ByUserIdOrderByUpdatedAtDesc(1L)).thenReturn(Collections.emptyList());

        float[] userVector = service.calculateUserVector(1L);

        assertThat(userVector).isNull();
    }

    @Test
    void timeDecayWeight_RecentInteractionHasHigherWeightThanOld() {
        double recentWeight = VectorMathUtil.calculateTimeDecayWeight(1.0, 0.01);
        double oldWeight = VectorMathUtil.calculateTimeDecayWeight(60.0, 0.01);

        assertThat(recentWeight).isGreaterThan(oldWeight);
        assertThat(recentWeight).isCloseTo(0.99, org.assertj.core.data.Offset.offset(0.02));
        assertThat(oldWeight).isCloseTo(0.55, org.assertj.core.data.Offset.offset(0.05));
    }

    @Test
    void calculateUserVector_MultipleInteractions_CalculatesNormalizedCentroid() {
        float[] movie1Vector = new float[384];
        float[] movie2Vector = new float[384];
        for (int i = 0; i < 384; i++) {
            movie1Vector[i] = 1.0f;
            movie2Vector[i] = 0.5f;
        }

        Movie m1 = Movie.builder().id(101L).title("Recent Movie").embedding(movie1Vector).build();
        Movie m2 = Movie.builder().id(102L).title("Old Movie").embedding(movie2Vector).build();

        UserMovieInteraction i1 = UserMovieInteraction.builder()
                .id(1L)
                .movie(m1)
                .isFavorite(true)
                .rating(5.0)
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        UserMovieInteraction i2 = UserMovieInteraction.builder()
                .id(2L)
                .movie(m2)
                .isFavorite(false)
                .rating(4.0)
                .updatedAt(LocalDateTime.now().minusDays(90))
                .build();

        when(interactionRepository.findTop20ByUserIdOrderByUpdatedAtDesc(1L)).thenReturn(List.of(i1, i2));

        float[] userVector = service.calculateUserVector(1L);

        assertThat(userVector).isNotNull();
        assertThat(userVector.length).isEqualTo(384);

        double sumSquares = 0.0;
        for (float v : userVector) {
            sumSquares += v * v;
        }
        assertThat(Math.sqrt(sumSquares)).isCloseTo(1.0, org.assertj.core.data.Offset.offset(0.001));
    }
}
