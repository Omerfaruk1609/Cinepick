package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendMatchResponseDto {
    private String user1Name;
    private String user2Name;
    private int friendshipMatchPercentage; // Örn: %84 Ortak Zevk Uyum Skoru
    private List<String> commonGenres;       // Ortak sevilen türler
    private List<MovieDto> recommendedMovies; // İkisinin de izlemediği ortak film önerileri
}
