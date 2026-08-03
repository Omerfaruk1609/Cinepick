package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class CachedUserVectorService {

    private final UserPreferenceVectorService vectorService;
    private final RedisTemplate<String, Object> redisTemplate;

    public float[] getUserVectorWithCache(Long userId) {
        String cacheKey = "user:vector:" + userId;
        try {
            float[] cachedVector = (float[]) redisTemplate.opsForValue().get(cacheKey);

            if (cachedVector != null) {
                log.info("User vector retrieved from Redis cache for userId: {}", userId);
                return cachedVector;
            }
        } catch (Exception e) {
            log.warn("Redis read failed for user vector cache: {}", e.getMessage());
        }

        float[] calculatedVector = vectorService.calculateUserVector(userId);
        if (calculatedVector != null) {
            try {
                // Hesaplanan vektörü Redis'te 2 saat önbellekle
                redisTemplate.opsForValue().set(cacheKey, calculatedVector, Duration.ofHours(2));
                log.info("User vector calculated and stored in Redis cache for userId: {}", userId);
            } catch (Exception e) {
                log.warn("Redis write failed for user vector cache: {}", e.getMessage());
            }
        }

        return calculatedVector;
    }

    public void evictUserVectorCache(Long userId) {
        String cacheKey = "user:vector:" + userId;
        try {
            Boolean deleted = redisTemplate.delete(cacheKey);
            log.info("Evicted user vector cache for userId: {}. Success: {}", userId, deleted);
        } catch (Exception e) {
            log.warn("Failed to evict user vector cache for userId {}: {}", userId, e.getMessage());
        }
    }
}
