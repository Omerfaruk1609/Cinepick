package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationExplanationService {

    private final ChatClient.Builder chatClientBuilder;

    public String generateReason(List<String> userFavoriteTitles, String recommendedMovieTitle, String recommendedMovieOverview) {
        String template = """
            Sen sinema konusunda uzman, samimi bir film rehberisin.
            Kullanıcının en sevdiği filmler: {favoriteMovies}
            Önerilen film: {targetMovie}
            Önerilen filmin özeti: {targetOverview}

            GÖREV: Kullanıcıya bu filmi NEDEN önerdiğini açıklayan TEK CÜMLELİK, heyecan verici ve kişiselleştirilmiş bir gerekçe yaz.
            KURALLAR:
            1. Yanıt kesinlikle tek bir cümle olmalı.
            2. 'Kullanıcı' deme, doğrudan kullanıcıya hitap et ("...sevdiğin için", "...tam senin kalemine göre").
            3. Geçmişte sevdiği filmlerden en alakalı olanına atıfta bulun.
            4. Tırnak işareti veya ekstra açıklama ekleme, sadece cümleyi dön.
            """;

        try {
            PromptTemplate promptTemplate = new PromptTemplate(template);
            Map<String, Object> variables = Map.of(
                    "favoriteMovies", userFavoriteTitles != null && !userFavoriteTitles.isEmpty() ? String.join(", ", userFavoriteTitles) : "Derin sinematik eserler",
                    "targetMovie", recommendedMovieTitle != null ? recommendedMovieTitle : "Bu Özel Film",
                    "targetOverview", recommendedMovieOverview != null ? recommendedMovieOverview : ""
            );

            ChatClient chatClient = chatClientBuilder.build();
            return chatClient.prompt(promptTemplate.create(variables))
                    .call()
                    .content()
                    .trim();
        } catch (Exception e) {
            log.warn("Spring AI gerekçe üretimi sırasında hata oluştu: {}", e.getMessage());
            String fav = (userFavoriteTitles != null && !userFavoriteTitles.isEmpty()) ? userFavoriteTitles.get(0) : "sinematik eserleri";
            return String.format("%s atmosferini ve temasını sevdiğin için %s tam senin kalemine göre!", fav, recommendedMovieTitle);
        }
    }
}
