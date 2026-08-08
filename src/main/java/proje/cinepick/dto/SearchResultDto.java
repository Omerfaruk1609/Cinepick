package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {

    private String query;
    private String mode;
    private List<MovieDto> results;
    private int totalResults;
    private int page;
    private int size;

    public static SearchResultDto empty(String query) {
        return SearchResultDto.builder()
                .query(query != null ? query : "")
                .mode("none")
                .results(Collections.emptyList())
                .totalResults(0)
                .page(0)
                .size(0)
                .build();
    }
}
