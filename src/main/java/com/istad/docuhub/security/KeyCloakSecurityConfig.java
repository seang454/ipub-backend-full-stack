package com.istad.docuhub.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.istad.docuhub.feature.user.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class KeyCloakSecurityConfig {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RefreshTokenService refreshTokenService;

    @Value("${backend.endpoint}")
    private String backendEndpoint;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Frontend origin
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // dev frontend
        configuration.setAllowCredentials(true); // must allow credentials (cookies)
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
        configuration.setExposedHeaders(List.of("Set-Cookie")); // optional, for debugging cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // enable CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register","/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"api/v1/auth/tokens","/api/v1/auth/protected-endpoint").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/auth/refresh/**").permitAll()
                        .requestMatchers("/favicon.ico", "/health").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/auth/refreshTokens").permitAll()
                        .requestMatchers("/api/v1/auth/keycloak/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/users/student").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/users/mentor").permitAll()

                        // Category Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/v1/categories").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()

                        // Media Endpoints
                        .requestMatchers(HttpMethod.GET,"/api/v1/media").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/media/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/media").hasAnyRole("STUDENT", "ADVISER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/media/**").hasAnyRole("STUDENT", "ADVISER", "ADMIN")

                        //Paper Endpoints
                        .requestMatchers(HttpMethod.POST,"/api/v1/papers").hasAnyRole("STUDENT", "ADMIN", "ADVISER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/papers/author").hasAnyRole("STUDENT", "ADMIN", "ADVISER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/papers/author/**").hasAnyRole("STUDENT", "ADMIN", "ADVISER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/papers/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/papers/published").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/papers/approved").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/papers/all").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/papers/pending").hasAnyRole("ADMIN")


                        .requestMatchers(HttpMethod.POST,"/api/v1/adviser_details").hasAnyRole("ADVISER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/adviser_details/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/v1/adviser_details/**").hasAnyRole("ADMIN", "ADVISER")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/adviser_details/**").hasAnyRole("ADMIN", "ADVISER")

                        // --- admin endpoints (all you listed) ---
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")

                        // thong admin approve or reject paper endpoint
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/paper/assign-adviser").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/paper/reassign-adviser").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/paper/reject").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/paper/**").hasAnyRole("ADMIN")

                        // by thong ( admin -create student, adviser, reject-user-reqeust-to-student and approve
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/student/create-student").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/adviser/create-adviser").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/student/approve-student-detail").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/admin/student/reject-student-detail").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/admin/student/").hasAnyRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/papers/pending").hasAnyRole("ADMIN")

                        // by thong user create requetform to promote to be a student need approve from admin
                        .requestMatchers(HttpMethod.POST, "/api/v1/user-promote/create-student-detail").hasAnyRole("USER")


                        .requestMatchers(HttpMethod.POST, "/api/v1/feedback").hasAnyRole("ADMIN", "ADVISER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/feedback").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/feedback/**").hasAnyRole("ADMIN", "ADVISER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(new OidcUserService()))
                        .successHandler((request, response, authentication) -> {
                            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                            OAuth2AuthorizedClient authorizedClient =
                                    authorizedClientService.loadAuthorizedClient("keycloak", authentication.getName());

                            if (authorizedClient != null) {
                                String accessToken = authorizedClient.getAccessToken().getTokenValue();
                                String idToken = oidcUser.getIdToken().getTokenValue();

                                // --- ACCESS TOKEN COOKIE ---
                                ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                                        .httpOnly(true)
                                        .secure(true)                   // ✅ required with SameSite=None
                                        .path("/")
                                        .maxAge(3600)
                                        .sameSite("None")               // ✅ cross-site allowed
                                        .domain(".docuhub.me")          // ✅ share across subdomains
                                        .build();

                                // --- ID TOKEN COOKIE ---
                                ResponseCookie idCookie = ResponseCookie.from("id_token", idToken)
                                        .httpOnly(true)
                                        .secure(true)
                                        .path("/")
                                        .maxAge(3600)
                                        .sameSite("None")
                                        .domain(".docuhub.me")
                                        .build();

                                // ✅ add cookies safely
                                response.addHeader("Set-Cookie", accessCookie.toString());
                                response.addHeader("Set-Cookie", idCookie.toString());
                            }

                            // ✅ Redirect to frontend (can be localhost for dev)
                            // In production: "https://frontend.docuhub.me"
                            response.sendRedirect("http://localhost:3000");
                        })
                )

                // JSON response for unauthenticated API requests
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint())
                )
                .oauth2Client(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Delete access_token and id_token cookies
                            deleteCookie(response, "access_token");
                            deleteCookie(response, "id_token");
                            deleteCookie(response, "JSESSIONID"); // optional
                            // Redirect to frontend or Keycloak logout
                            String keycloakLogoutUrl = "https://keycloak.docuhub.me/realms/docuapi/protocol/openid-connect/logout";
                            String frontendRedirect = "http://localhost:3000"; // must match Keycloak redirect URI

                            response.sendRedirect(keycloakLogoutUrl + "?redirect_uri=" + frontendRedirect);
                        })
                        .invalidateHttpSession(true)
                );
        return http.build();
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)           // must match original cookie
                .path("/")
                .domain(".docuhub.me")  // must match original domain
                .maxAge(0)
                .sameSite("None")       // must match original SameSite
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    // JWT -> Spring roles converter
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        Converter<Jwt, Collection<GrantedAuthority>> converter = jwt -> {
            Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
            Collection<String> roles = realmAccess != null
                    ? realmAccess.getOrDefault("roles", List.of())
                    : List.of();
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        };
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }

    // JSON AuthenticationEntryPoint for APIs
    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, Object> error = Map.of(
                    "error", "Authentication required",
                    "message", authException.getMessage()
            );
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        };
    }

    // 🔹 Auto refresh token manager
    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService clientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(clientService);
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clients,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider provider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()  // standard login
                        .refreshToken()       // 🔥 enables automatic refresh
                        .build();

        DefaultOAuth2AuthorizedClientManager manager =
                new DefaultOAuth2AuthorizedClientManager(clients, authorizedClientRepository);
        manager.setAuthorizedClientProvider(provider);

        return manager;
    }

}
