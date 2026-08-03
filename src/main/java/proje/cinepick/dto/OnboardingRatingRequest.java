package proje.cinepick.dto;

public record OnboardingRatingRequest(
    Long movieId,
    String preference // "LIKE", "DISLIKE", "SKIP"
) {}
