package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proje.cinepick.entity.User;
import proje.cinepick.entity.UserMovieInteraction;
import proje.cinepick.repository.UserMovieInteractionRepository;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.util.VectorMathUtil;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferenceVectorService {

    private final UserMovieInteractionRepository interactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public float[] calculateUserVector(Long userId) {
        List<UserMovieInteraction> interactions = interactionRepository.findTop20ByUserIdOrderByUpdatedAtDesc(userId);

        if (interactions.isEmpty()) {
            return null; // Soğuk başlama durumu (Henüz etkileşim yok)
        }

        List<float[]> vectorList = new ArrayList<>();
        List<Float> weightList = new ArrayList<>();

        for (UserMovieInteraction interaction : interactions) {
            if (interaction.getMovie() == null) continue;

            float[] movieVector = interaction.getMovie().getEmbedding();
            if (movieVector == null) continue;

            float weight = calculateWeight(interaction);
            vectorList.add(movieVector);
            weightList.add(weight);
        }

        if (vectorList.isEmpty()) return null;

        float[][] vectorsArray = vectorList.toArray(new float[0][]);
        float[] weightsArray = new float[weightList.size()];
        for (int i = 0; i < weightList.size(); i++) {
            weightsArray[i] = weightList.get(i);
        }

        float[] userVector = VectorMathUtil.calculateWeightedCentroid(vectorsArray, weightsArray);

        // Kullanıcının profil vektörünü veritabanında sakla
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
            else if (interaction.getRating() <= 2.0) weight -= 1.0f; // Negatif katsayı etkisi
        }
        return weight;
    }
}
