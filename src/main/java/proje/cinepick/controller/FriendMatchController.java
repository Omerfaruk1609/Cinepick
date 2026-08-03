package proje.cinepick.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import proje.cinepick.dto.FriendMatchResponseDto;
import proje.cinepick.entity.User;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.service.FriendMatchService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FriendMatchController {

    private final FriendMatchService friendMatchService;
    private final UserRepository userRepository;

    @GetMapping("/friend-match")
    public ResponseEntity<FriendMatchResponseDto> getFriendMatch(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String friendUsername) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseGet(() -> userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername())));

        FriendMatchResponseDto result = friendMatchService.calculateFriendMatch(user.getId(), friendUsername);
        return ResponseEntity.ok(result);
    }
}
