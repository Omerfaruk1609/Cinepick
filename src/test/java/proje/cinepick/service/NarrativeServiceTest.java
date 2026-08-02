package proje.cinepick.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import proje.cinepick.buisness.NarrativeEngineManager;
import proje.cinepick.dto.MovieAnalysisRequest;
import proje.cinepick.dto.MovieAnalysisResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NarrativeServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private NarrativeEngineManager fallbackNarrativeEngine;

    @InjectMocks
    private SpringAiNarrativeService springAiNarrativeService;

    private MovieAnalysisRequest request;

    @BeforeEach
    void setUp() {
        request = MovieAnalysisRequest.builder()
                .tmdbId(550L)
                .title("Fight Club")
                .overview("An insomniac office worker and a devil-may-care soap maker form an underground fight club.")
                .genres(List.of("Drama"))
                .runtime(139)
                .voteAverage(8.4)
                .releaseYear(1999)
                .build();
    }

    @Test
    void shouldFallbackToRuleBasedEngineWhenLlmFails() {
        when(chatModel.call(any())).thenThrow(new RuntimeException("LLM Timeout"));

        MovieAnalysisResponse expectedFallback = MovieAnalysisResponse.builder()
                .atmosphere("Karanlık ve Gergin")
                .narrativePace("Dinamik ve Hızlı")
                .keyThemes(List.of("Tüketim Toplumu", "Kimlik Arayışı"))
                .whyToWatch("Derin kurgusu ile kaçırılmaması gereken bir eserdir.")
                .build();

        when(fallbackNarrativeEngine.analyzeMovie(any(MovieAnalysisRequest.class))).thenReturn(expectedFallback);

        MovieAnalysisResponse result = springAiNarrativeService.analyzeMovie(request);

        assertNotNull(result);
        assertEquals("Karanlık ve Gergin", result.getAtmosphere());
        verify(fallbackNarrativeEngine, times(1)).analyzeMovie(any(MovieAnalysisRequest.class));
    }
}
