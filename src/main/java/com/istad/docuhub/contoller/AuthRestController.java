package com.istad.docuhub.contoller;

import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.UserCreateDto;
import com.istad.docuhub.feature.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody UserCreateDto userCreateDto) {
        log.info("Registering user {}", userCreateDto);
        return userService.register(userCreateDto);
    }
    @GetMapping("/tokens")
    public ResponseEntity<?> getTokens(
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        if (client == null || oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required. Please log in via /api/v1/auth/login"));
        }

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", client.getAccessToken().getTokenValue());
        tokens.put("refreshToken", client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null);
        tokens.put("idToken", oidcUser.getIdToken().getTokenValue());
        tokens.put("claims", oidcUser.getClaims());
        return ResponseEntity.ok(tokens);
    }
    @GetMapping("users")
    public List<UserResponse> getUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("user/{id}")
    public UserResponse getSingleUser(@PathVariable String id){
        return userService.getSingleUser(id);
    }
    @GetMapping("slug")
    public List<UserResponse> searchUserByUsername(@RequestParam String username){
        return userService.searchUserByUsername(username);
    }

}
