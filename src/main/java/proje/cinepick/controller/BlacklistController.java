package proje.cinepick.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import proje.cinepick.entity.User;
import proje.cinepick.entity.UserBlacklist;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.service.BlacklistService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<UserBlacklist> getBlacklist(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(blacklistService.getBlacklist(user.getId()));
    }

    @PostMapping("/genres")
    public ResponseEntity<UserBlacklist> updateExcludedGenres(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, List<String>> body) {
        User user = getUser(userDetails);
        List<String> excludedGenres = body.get("excludedGenres");
        return ResponseEntity.ok(blacklistService.updateExcludedGenres(user.getId(), excludedGenres));
    }

    @PostMapping("/add-genre")
    public ResponseEntity<Void> addGenreToBlacklist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        User user = getUser(userDetails);
        String genre = body.get("genre");
        blacklistService.addGenreToBlacklist(user.getId(), genre);
        return ResponseEntity.ok().build();
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseGet(() -> userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername())));
    }
}
