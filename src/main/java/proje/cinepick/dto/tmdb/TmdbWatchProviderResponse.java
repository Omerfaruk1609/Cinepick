package proje.cinepick.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TmdbWatchProviderResponse {
    private Map<String, CountryProviders> results;

    @Data
    public static class CountryProviders {
        private String link; // JustWatch direkt yönlendirme linki
        private List<ProviderDetail> flatrate; // Abonelikle izlenenler (Netflix, Prime vb.)
        private List<ProviderDetail> rent;     // Kiralama seçenekleri
        private List<ProviderDetail> buy;      // Satın alma seçenekleri
    }

    @Data
    public static class ProviderDetail {
        @JsonProperty("provider_id")
        private Long providerId;

        @JsonProperty("provider_name")
        private String providerName;

        @JsonProperty("logo_path")
        private String logoPath;
    }
}
