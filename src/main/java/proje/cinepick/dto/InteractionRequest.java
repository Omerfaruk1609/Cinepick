package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionRequest {
    private Long movieId;
    private Boolean isFavorite;
    private Boolean inWatchlist;
    private Double rating;
}
