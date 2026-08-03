package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proje.cinepick.entity.UserBlacklist;
import proje.cinepick.repository.UserBlacklistRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final UserBlacklistRepository blacklistRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public UserBlacklist getBlacklist(Long userId) {
        return blacklistRepository.findByUserId(userId)
                .orElseGet(() -> UserBlacklist.builder()
                        .userId(userId)
                        .excludedGenres(new String[0])
                        .excludedDirectors(new String[0])
                        .build());
    }

    @Transactional
    public UserBlacklist updateExcludedGenres(Long userId, List<String> genres) {
        UserBlacklist blacklist = blacklistRepository.findByUserId(userId)
                .orElseGet(() -> UserBlacklist.builder()
                        .userId(userId)
                        .excludedGenres(new String[0])
                        .excludedDirectors(new String[0])
                        .build());

        String[] genreArray = genres != null ? genres.toArray(new String[0]) : new String[0];
        blacklist.setExcludedGenres(genreArray);

        UserBlacklist saved = blacklistRepository.save(blacklist);
        evictRecommendationCache(userId);
        return saved;
    }

    @Transactional
    public void addGenreToBlacklist(Long userId, String genre) {
        UserBlacklist blacklist = blacklistRepository.findByUserId(userId)
                .orElseGet(() -> UserBlacklist.builder()
                        .userId(userId)
                        .excludedGenres(new String[0])
                        .excludedDirectors(new String[0])
                        .build());

        String[] updatedGenres = addElementToArray(blacklist.getExcludedGenres(), genre);
        blacklist.setExcludedGenres(updatedGenres);

        blacklistRepository.save(blacklist);
        evictRecommendationCache(userId);
    }

    private void evictRecommendationCache(Long userId) {
        try {
            redisTemplate.delete("user:rec:" + userId);
            log.info("Evicted recommendation cache for userId: {}", userId);
        } catch (Exception e) {
            log.warn("Failed to evict recommendation cache for userId: {}", userId, e);
        }
    }

    private String[] addElementToArray(String[] array, String element) {
        if (array == null) return new String[]{element};
        for (String s : array) {
            if (s.equalsIgnoreCase(element)) return array;
        }
        String[] newArray = new String[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = element;
        return newArray;
    }
}
