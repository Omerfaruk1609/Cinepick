package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncExplanationService {

    private final RecommendationExplanationService explanationService;
    private final RedisTemplate<String, Object> redisTemplate;

    public String getOrGenerateReason(Long userId, String targetMovieTitle, List<String> favoriteMovies, String overview) {
        if (targetMovieTitle == null) return null;

        String cacheKey = "rec:reason:" + userId + ":" + targetMovieTitle.hashCode();

        try {
            String cachedReason = (String) redisTemplate.opsForValue().get(cacheKey);
            if (cachedReason != null) {
                return cachedReason;
            }
        } catch (Exception e) {
            log.warn("Redis read failed for recommendation reason: {}", e.getMessage());
        }

        String generatedReason = explanationService.generateReason(favoriteMovies, targetMovieTitle, overview);

        try {
            redisTemplate.opsForValue().set(cacheKey, generatedReason, Duration.ofDays(7));
        } catch (Exception e) {
            log.warn("Redis write failed for recommendation reason: {}", e.getMessage());
        }

        return generatedReason;
    }
}
