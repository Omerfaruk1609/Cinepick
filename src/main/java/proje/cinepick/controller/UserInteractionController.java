package proje.cinepick.controller;

import proje.cinepick.dto.InteractionRequest;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.dto.OnboardingRatingRequest;
import proje.cinepick.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/interactions")
@RequiredArgsConstructor
public class UserInteractionController {

    private final UserInteractionService interactionService;

    // Etkileşim Güncelle (Favori/Watchlist Toggle veya Puan Verme)
    @PostMapping("/toggle")
    public ResponseEntity<Void> toggleInteraction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody InteractionRequest request) {

        interactionService.updateInteraction(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/onboarding")
    public ResponseEntity<Void> processOnboarding(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<OnboardingRatingRequest> ratings) {

        interactionService.saveOnboardingRatings(userDetails.getUsername(), ratings);
        return ResponseEntity.ok().build();
    }

    // Kullanıcının Favori Filmleri
    @GetMapping("/favorites")
    public ResponseEntity<List<MovieDto>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(interactionService.getUserFavorites(userDetails.getUsername()));
    }

    // Kullanıcının İzleme Listesi
    @GetMapping("/watchlist")
    public ResponseEntity<List<MovieDto>> getWatchlist(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(interactionService.getUserWatchlist(userDetails.getUsername()));
    }
}
