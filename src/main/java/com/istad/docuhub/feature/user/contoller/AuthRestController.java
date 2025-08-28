package com.istad.docuhub.feature.user.contoller;

import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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
    public ResponseEntity<TokenResponseRecord> getTokens(
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        if (client == null || oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }

        TokenResponseRecord tokenResponse = new TokenResponseRecord(
                client.getAccessToken().getTokenValue(),
                client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null,
                oidcUser.getIdToken().getTokenValue(),
                oidcUser.getClaims()
        );

        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/refreshTokens")
    public ResponseEntity<TokenResponseRecord> refreshTokens(
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client,
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        if (client == null || oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Map<String, Object> tokens = userService.getValidTokens(client, oidcUser);
        if (tokens.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        TokenResponseRecord tokenResponse = new TokenResponseRecord(
                (String) tokens.get("accessToken"),
                (String) tokens.get("refreshToken"),
                (String) tokens.get("idToken"),
                (Map<String, Object>) tokens.get("claims")
        );

        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("users")
    public List<UserResponse> getUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("user/{uuid}")
    public UserResponse getSingleUser(@PathVariable String uuid){
        return userService.getSingleUser(uuid);
    }
    @GetMapping("slug")
    public List<UserResponse> searchUserByUsername(@RequestParam String username){
        return userService.searchUserByUsername(username);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("user/{uuid}")
    public void deleteUser(@PathVariable String uuid) {
        log.info("User id controller {} ",uuid);
         userService.deleteUser(uuid);
    }
    @PatchMapping("/user/{uuid}")
    public void updateUser(@PathVariable String uuid, @RequestBody UpdateUserDto updateUserDto) {
        log.info("User id controller {} ",updateUserDto);
        userService.updateUser(uuid, updateUserDto);
    }
    @PutMapping("user/{uuid}")
    public UpdateUserImageDto updateProfileImage(@PathVariable String uuid,@RequestBody UpdateUserImageDto updateUserImageDto) {
        return userService.updateImageUrl(updateUserImageDto.imageUrl(),uuid);
    }
}
