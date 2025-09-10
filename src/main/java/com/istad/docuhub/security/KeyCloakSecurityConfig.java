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
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000","https://new-add-to-card-hw-v1ia.vercel.app/")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register","/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"api/v1/auth/tokens").permitAll()
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
                            OAuth2AuthorizedClient authorizedClient = authorizedClientService
                                    .loadAuthorizedClient("keycloak", authentication.getName());

                            if (authorizedClient != null) {
                                String accessToken = authorizedClient.getAccessToken().getTokenValue();
                                String idToken = oidcUser.getIdToken().getTokenValue();
                                String refreshToken = authorizedClient.getRefreshToken() != null
                                        ? authorizedClient.getRefreshToken().getTokenValue()
                                        : null;

                                // Cookie settings for local dev
                                Cookie accessCookie = new Cookie("access_token", accessToken);
                                accessCookie.setHttpOnly(true); // secure from JS
                                accessCookie.setSecure(false); // allow HTTP localhost
                                accessCookie.setPath("/");
                                accessCookie.setMaxAge(3600); // 1 hour
                                accessCookie.setDomain("https://new-add-to-card-hw-v1ia.vercel.app");
                                response.addCookie(accessCookie);

                                Cookie idCookie = new Cookie("id_token", idToken);
                                idCookie.setHttpOnly(true);
                                idCookie.setSecure(false);
                                idCookie.setPath("/");
                                idCookie.setMaxAge(3600);
                                idCookie.setDomain("https://new-add-to-card-hw-v1ia.vercel.app");
                                response.addCookie(idCookie);

                                if (refreshToken != null) {
                                    refreshTokenService.storeToken(authentication.getName(), refreshToken, 86400);
                                }
                            }

                            response.sendRedirect("https://new-add-to-card-hw-v1ia.vercel.app");
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
                            // Keycloak logout endpoint
                            String keycloakLogoutUrl = "https://keycloak.docuhub.me/realms/docuapi/protocol/openid-connect/logout";
                            // Redirect back to your backend endpoint after logout
                            String redirectAfterLogout = "http://localhost:3000";
                            // Full logout URL
                            String logoutUrl = keycloakLogoutUrl + "?redirect_uri=" + redirectAfterLogout;
                            // Redirect browser to Keycloak logout
                            response.sendRedirect(logoutUrl);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
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
