package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proje.cinepick.dto.FriendMatchResponseDto;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.entity.Movie;
import proje.cinepick.entity.User;
import proje.cinepick.repository.MovieRepository;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.util.VectorMathUtil;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendMatchService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CachedUserVectorService userVectorService;

    @Transactional(readOnly = true)
    public FriendMatchResponseDto calculateFriendMatch(Long user1Id, String friendUsername) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + user1Id));

        User user2 = userRepository.findByUsername(friendUsername)
                .orElseGet(() -> userRepository.findByEmail(friendUsername)
                        .orElseThrow(() -> new RuntimeException("Arkadaş bulunamadı: " + friendUsername)));

        Long user2Id = user2.getId();

        // 1. İki Kullanıcının Profil Vektörlerini Çek
        float[] vector1 = userVectorService.getUserVectorWithCache(user1Id);
        float[] vector2 = userVectorService.getUserVectorWithCache(user2Id);

        // Soğuk Başlama Kontrolü
        if (vector1 == null || vector2 == null) {
            List<Movie> popularMovies = movieRepository.findTop10ByOrderByVoteAverageDesc();
            List<MovieDto> fallbackDtos = popularMovies.stream().map(MovieDto::fromEntity).toList();

            return FriendMatchResponseDto.builder()
                    .user1Name(user1.getUsername())
                    .user2Name(user2.getUsername())
                    .friendshipMatchPercentage(75)
                    .commonGenres(List.of("Sinema", "Popüler Eserler"))
                    .recommendedMovies(fallbackDtos)
                    .build();
        }

        // 2. Kosinüs Benzerliği Üzerinden Zevk Uyum Yüzdesini Hesapla
        double similarity = VectorMathUtil.cosineSimilarity(vector1, vector2);
        int matchPercentage = (int) Math.round(similarity * 100);
        matchPercentage = Math.max(10, Math.min(99, matchPercentage));

        // 3. Ortak Grup Vektörünü Hesapla ($V_group)
        float[] groupVector = VectorMathUtil.calculateWeightedCentroid(
                new float[][]{vector1, vector2},
                new float[]{1.0f, 1.0f}
        );

        // 4. İki Kullanıcının da İzlemediği Ortak Filmleri Çek
        List<Movie> groupMovies = movieRepository.findGroupRecommendations(
                user1Id,
                user2Id,
                Arrays.toString(groupVector),
                10
        );

        List<MovieDto> movieDtos = groupMovies.stream().map(MovieDto::fromEntity).toList();

        return FriendMatchResponseDto.builder()
                .user1Name(user1.getUsername())
                .user2Name(user2.getUsername())
                .friendshipMatchPercentage(matchPercentage)
                .recommendedMovies(movieDtos)
                .build();
    }
}
