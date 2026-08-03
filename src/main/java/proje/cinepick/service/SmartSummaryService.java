package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import proje.cinepick.dto.MovieSmartSummaryDto;
import proje.cinepick.entity.Movie;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartSummaryService {

    private final ChatClient.Builder chatClientBuilder;
    private final RedisTemplate<String, Object> redisTemplate;

    public MovieSmartSummaryDto getOrGenerateSmartSummary(Movie movie) {
        if (movie == null) return null;

        String cacheKey = "movie:summary:" + movie.getId();

        // 1. Önbellek Kontrolü (Redis)
        try {
            MovieSmartSummaryDto cached = (MovieSmartSummaryDto) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("Smart summary retrieved from Redis cache for movieId: {}", movie.getId());
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis read failed for smart summary: {}", e.getMessage());
        }

        // 2. Structured Output Converter Tanımlama
        BeanOutputConverter<MovieSmartSummaryDto> converter = new BeanOutputConverter<>(MovieSmartSummaryDto.class);

        String template = """
            Sen profesyonel bir sinema eleştirmenisin. Aşağıdaki film için SPOILER İÇERMEYEN 30 saniyelik akıllı bir analiz çıkar.

            Film Adı: {title}
            Orijinal Özet: {overview}
            Türler: {genres}

            İstenen Format:
            {format}

            KURALLAR:
            1. KESİNLİKLE SPOILER VERME. Sadece atmosfer, tempo ve temaları değerlendir.
            2. 'thirtySecondOverview' alanına filmin ruhunu anlatan 2-3 cümlelik vurucu bir paragraf yaz.
            3. 'keyHighlights' alanına filmin öne çıkan 3 en güçlü yönünü yaz.
            4. 'forWhom' alanına bu filmi kimlerin seveceğini (2 madde), 'notForWhom' alanına kimlerin sıkılabileceğini (2 madde) yaz.
            """;

        try {
            String genresStr = movie.getGenres() != null ? String.join(", ", movie.getGenres()) : "Genel Sinema";
            PromptTemplate promptTemplate = new PromptTemplate(template);
            Map<String, Object> variables = Map.of(
                    "title", movie.getTitle() != null ? movie.getTitle() : "Film",
                    "overview", movie.getOverview() != null ? movie.getOverview() : "",
                    "genres", genresStr,
                    "format", converter.getFormat()
            );

            ChatClient chatClient = chatClientBuilder.build();
            String response = chatClient.prompt(promptTemplate.create(variables))
                    .call()
                    .content();

            MovieSmartSummaryDto summaryDto = converter.convert(response);

            if (summaryDto != null) {
                // 3. Hesaplanan Analizi Redis'te 30 Gün Sakla
                try {
                    redisTemplate.opsForValue().set(cacheKey, summaryDto, Duration.ofDays(30));
                } catch (Exception e) {
                    log.warn("Redis write failed for smart summary: {}", e.getMessage());
                }
                return summaryDto;
            }
        } catch (Exception e) {
            log.warn("Spring AI smart summary üretimi sırasında hata oluştu: {}", e.getMessage());
        }

        // Fallback akıllı özet
        return createFallbackSummary(movie);
    }

    private MovieSmartSummaryDto createFallbackSummary(Movie movie) {
        String title = movie.getTitle() != null ? movie.getTitle() : "Bu eser";
        String genres = movie.getGenres() != null ? String.join(", ", movie.getGenres()) : "Sinema";

        return MovieSmartSummaryDto.builder()
                .thirtySecondOverview(String.format("%s, %s türündeki özgün kurgusu, atmosferik görselliği ve etkileyici temalarıyla izleyiciyi içine çeken spoilersız dinamik bir sinema deneyimi sunuyor.", title, genres))
                .keyHighlights(List.of("Atmosferik kurgu ve tempo", "Etkileyici sinematografi", "Derin karakter arkları"))
                .forWhom(List.of(
                        genres + " türüne ilgi duyan ve tematik derinlik arayanlar.",
                        "Görsel atmosferi ve hikaye anlatımını seven sinemaseverler."
                ))
                .notForWhom(List.of(
                        "Yavaş gelişen kurgulara sabredemeyen ve yüzeysel çerezlik içerik arayanlar.",
                        "Geleneksel aksiyon kalıplarının dışına çıkmak istemeyenler."
                ))
                .build();
    }
}
