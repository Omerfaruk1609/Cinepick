package proje.cinepick.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proje.cinepick.dto.TasteAnalyticsDto;
import proje.cinepick.entity.User;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.service.TasteAnalyticsService;

@RestController
@RequestMapping("/api/v1/users/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final TasteAnalyticsService tasteAnalyticsService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<TasteAnalyticsDto> getUserTasteAnalytics(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseGet(() -> userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername())));

        TasteAnalyticsDto analytics = tasteAnalyticsService.getUserTasteAnalytics(user.getId());
        return ResponseEntity.ok(analytics);
    }
}
