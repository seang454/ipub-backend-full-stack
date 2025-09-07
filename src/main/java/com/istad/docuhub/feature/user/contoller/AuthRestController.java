package com.istad.docuhub.feature.user.contoller;

import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.user.KeycloakAuthService;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;
    private final KeycloakAuthService keycloakAuthService;

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
    @GetMapping("users/page")
    Page<UserResponse> getAllActiveUsers( @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return userService.getAllUsersByPage(page, size);
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
    @DeleteMapping("/user/{uuid}")
    public void deleteUser(@PathVariable String uuid) {
        log.info("User id controller {} ",uuid);
         userService.deleteUser(uuid);
    }
    @PatchMapping("/user/{uuid}")
    public void updateUser(@PathVariable String uuid, @RequestBody UpdateUserDto updateUserDto) {
        log.info("User id controller {} ",updateUserDto);
        userService.updateUser(uuid, updateUserDto);
    }
    @PutMapping("/user/{uuid}")
    public UpdateUserImageDto updateProfileImage(@PathVariable String uuid,@RequestBody UpdateUserImageDto updateUserImageDto) {
        return userService.updateImageUrl(updateUserImageDto.imageUrl(),uuid);
    }
    @GetMapping("/user")
    public List<UserResponse> getAllUsers(){
        return userService.getAllPublicUser();
    }
    @GetMapping("/user/student")
    public List<UserResponse> getAllStudents(){
        return userService.getAllStudent();
    }
    @GetMapping ("/user/mentor")
    public List<UserResponse> getAllMentors(){
        return userService.getAllMentor();
    }
    @PostMapping("/user/student/{uuid}")
    public void promoteStudent(@PathVariable String uuid) {
        log.info("User id controller {} ",uuid);
        userService.promoteAsStudent(uuid);
    }
    @PostMapping("/user/mentor/{uuid}")
    public void promoteMentor(@PathVariable String uuid) {
        userService.promoteAsMentor(uuid);
    }
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Logindto login) {
        Map<String, Object> tokenResponse = keycloakAuthService.login(login.username(), login.password());

        // You can also return only the access token if you want:
        // return Map.of("access_token", tokenResponse.get("access_token"));

        return tokenResponse; // returns JSON with access_token, refresh_token, etc.
    }

    @GetMapping("/user/currentId")
    public CurrentUser getCurrentUser() {
        return userService.getCurrentUserSub();
    }
}
