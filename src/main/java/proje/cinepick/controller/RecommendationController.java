package proje.cinepick.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.entity.User;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.service.PersonalizedRecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final PersonalizedRecommendationService recommendationService;
    private final UserRepository userRepository;

    @GetMapping("/personalized")
    public ResponseEntity<List<MovieDto>> getPersonalizedRecommendations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(defaultValue = "10") int limit) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseGet(() -> userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found")));

        List<MovieDto> recommendations = recommendationService.getPersonalizedRecommendations(
                user.getId(),
                genres,
                limit
        );

        return ResponseEntity.ok(recommendations);
    }
}
