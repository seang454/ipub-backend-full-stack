package com.istad.docuhub.feature.user.contoller;

import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.user.KeycloakAuthService;
import com.istad.docuhub.feature.user.RefreshTokenService;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.*;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;
    private final KeycloakAuthService keycloakAuthService;
    private final RefreshTokenService refreshTokenService;
    private final RestTemplate restTemplate = new RestTemplate();
    // to verify token
    private static final String JWKS_URL = "https://keycloak.docuhub.me/realms/docuapi/protocol/openid-connect/certs";


    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUri;

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
    @GetMapping("/protected-endpoint")
    public ResponseEntity<Map<String, Object>> protectedEndpoint(
            @CookieValue(name = "access_token", required = false) String token) {

        Map<String, Object> response = new HashMap<>();

        if (token == null) {
            response.put("status", "unauthenticated");
            response.put("no", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "No token provided");
            response.put("token", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            // Decode payload without verifying signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new JwtException("Invalid JWT format");
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            Claims claims = Jwts.parserBuilder()
                    .build()
                    .parseClaimsJwt(parts[0] + "." + parts[1] + ".") // parseClaimsJwt ignores signature
                    .getBody();

            // Check expiration
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                response.put("status", "unauthenticated");
                response.put("no", HttpStatus.UNAUTHORIZED.value());
                response.put("message", "Token expired");
                response.put("token", token);
                response.put("claims", claims);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Token is not expired
            response.put("status", "authenticated");
            response.put("no", HttpStatus.OK.value());
            response.put("message", "Token is valid (not expired)");
            response.put("token", token);
            response.put("claims", claims);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "unauthenticated");
            response.put("no", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Invalid token: " + e.getMessage());
            response.put("token", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String username, HttpServletResponse response) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }

        String storedRefreshToken = refreshTokenService.getToken(username);
        if (storedRefreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token found");
        }

        // Prepare Keycloak token request
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", storedRefreshToken);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, Map.class);

            Map<String, Object> body = tokenResponse.getBody();
            if (body == null || !body.containsKey("access_token")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to refresh token");
            }

            String newAccessToken = (String) body.get("access_token");
            String newRefreshToken = (String) body.get("refresh_token");
            String idToken = (String) body.get("id_token");

            // Update stored refresh token
            if (newRefreshToken != null) {
                refreshTokenService.storeToken(username, newRefreshToken, 86400);
            }

            // Update access_token cookie
            Cookie accessCookie = new Cookie("access_token", newAccessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(3600);
            response.addCookie(accessCookie);

            // Optional: update id_token cookie
            if (idToken != null) {
                Cookie idCookie = new Cookie("id_token", idToken);
                idCookie.setHttpOnly(true);
                idCookie.setSecure(true);
                idCookie.setPath("/");
                idCookie.setMaxAge(3600);
                response.addCookie(idCookie);
            }

            return ResponseEntity.ok(Map.of("status", "refreshed"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh failed: " + e.getMessage());
        }
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
