package com.istad.docuhub.feature.user.contoller;

import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.UpdateUserDto;
import com.istad.docuhub.feature.user.dto.UpdateUserImageDto;
import com.istad.docuhub.feature.user.dto.UserCreateDto;
import com.istad.docuhub.feature.user.dto.UserResponse;
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

    @GetMapping("/refreshTokens")
    public void refreshTokens(
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient client,
            @AuthenticationPrincipal OidcUser oidcUser,
            HttpServletResponse response
    ) throws IOException {

        if (client == null || oidcUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        // Refresh tokens
        Map<String, Object> tokens = userService.getValidTokens(client, oidcUser);

        if (tokens.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Could not refresh tokens. Please login again.");
            return;
        }

        // Redirect to /tokens endpoint to return the refreshed tokens
        response.sendRedirect("/api/v1/auth/tokens");
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
