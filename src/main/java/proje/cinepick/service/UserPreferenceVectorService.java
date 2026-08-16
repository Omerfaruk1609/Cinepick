package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proje.cinepick.entity.UserMovieInteraction;
import proje.cinepick.repository.UserMovieInteractionRepository;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.util.VectorMathUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferenceVectorService {

    private final UserMovieInteractionRepository interactionRepository;
    private final UserRepository userRepository;

    private static final double LAMBDA_DECAY = 0.01; // Daily decay factor

    @Transactional
    public float[] calculateUserVector(Long userId) {
        List<UserMovieInteraction> interactions = interactionRepository.findTop20ByUserIdOrderByUpdatedAtDesc(userId);

        if (interactions == null || interactions.isEmpty()) {
            log.info("Cold start user (userId: {}), no interactions found", userId);
            return null;
        }

        List<float[]> vectorList = new ArrayList<>();
        List<Float> weightList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (UserMovieInteraction interaction : interactions) {
            if (interaction.getMovie() == null) continue;

            float[] movieVector = interaction.getMovie().getEmbedding();
            if (movieVector == null || movieVector.length == 0) continue;

            float baseWeight = calculateWeight(interaction);
            if (baseWeight <= 0.0f) continue;

            long daysDiff = 0;
            if (interaction.getUpdatedAt() != null) {
                daysDiff = Math.max(0, Duration.between(interaction.getUpdatedAt(), now).toDays());
            }

            float decayWeight = VectorMathUtil.calculateTimeDecayWeight(daysDiff, LAMBDA_DECAY);
            float finalWeight = baseWeight * decayWeight;

            vectorList.add(movieVector);
            weightList.add(finalWeight);
        }

        if (vectorList.isEmpty()) {
            log.info("Cold start user (userId: {}), no valid movie embeddings in interaction history", userId);
            return null;
        }

        float[][] vectorsArray = vectorList.toArray(new float[0][]);
        float[] weightsArray = new float[weightList.size()];
        for (int i = 0; i < weightList.size(); i++) {
            weightsArray[i] = weightList.get(i);
        }

        float[] userVector = VectorMathUtil.calculateWeightedCentroid(vectorsArray, weightsArray);

        userRepository.findById(userId).ifPresent(user -> {
            user.setUserVector(userVector);
            userRepository.save(user);
        });

        return userVector;
    }

    public float calculateWeight(UserMovieInteraction interaction) {
        float weight = 0.0f;
        if (interaction.isFavorite()) weight += 1.5f;
        if (interaction.isInWatchlist()) weight += 0.8f;

        if (interaction.getRating() != null) {
            if (interaction.getRating() >= 4.0) weight += 1.2f;
            else if (interaction.getRating() <= 2.0) weight -= 1.0f;
        }
        return Math.max(0.0f, weight);
    }
}
