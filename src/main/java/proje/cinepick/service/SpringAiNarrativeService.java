package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import proje.cinepick.buisness.NarrativeEngineManager;
import proje.cinepick.dto.MovieAnalysisRequest;
import proje.cinepick.dto.MovieAnalysisResponse;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAiNarrativeService {

    private final ChatModel chatModel;
    private final NarrativeEngineManager fallbackNarrativeEngine;

    private static final String SYSTEM_PROMPT = """
            You are a world-class film critic and cinematic analyst.
            Analyze the provided movie details and generate a structured JSON response evaluating its atmosphere, narrative pace, key themes, and a compelling recommendation rationale.
            
            Strictly follow the formatting instructions provided below.
            {format}
            """;

    private static final String USER_PROMPT = """
            Analyze the following movie:
            Title: {title}
            Overview: {overview}
            Genres: {genres}
            Runtime: {runtime} minutes
            Release Year: {releaseYear}
            Rating: {voteAverage}
            """;

    public MovieAnalysisResponse analyzeMovie(MovieAnalysisRequest request) {
        try {
            BeanOutputConverter<MovieAnalysisResponse> converter = new BeanOutputConverter<>(MovieAnalysisResponse.class);

            SystemPromptTemplate systemTemplate = new SystemPromptTemplate(SYSTEM_PROMPT);
            Message systemMessage = systemTemplate.createMessage(Map.of("format", converter.getFormat()));

            PromptTemplate userTemplate = new PromptTemplate(USER_PROMPT);
            Message userMessage = userTemplate.createMessage(Map.of(
                    "title", request.getTitle() != null ? request.getTitle() : "Unknown Title",
                    "overview", request.getOverview() != null ? request.getOverview() : "",
                    "genres", request.getGenres() != null ? String.join(", ", request.getGenres()) : "",
                    "runtime", request.getRuntime() != null ? request.getRuntime() : 0,
                    "releaseYear", request.getReleaseYear() != null ? request.getReleaseYear() : 0,
                    "voteAverage", request.getVoteAverage() != null ? request.getVoteAverage() : 0.0
            ));

            Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

            String rawResponse = chatModel.call(prompt).getResult().getOutput().getContent();
            return converter.convert(rawResponse);
        } catch (Exception ex) {
            log.warn("LLM service call failed or timed out. Executing rule-based fallback mechanism.", ex);
            return fallbackNarrativeEngine.analyzeMovie(request);
        }
    }
}
