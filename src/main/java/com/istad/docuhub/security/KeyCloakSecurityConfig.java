package com.istad.docuhub.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class KeyCloakSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/auth/tokens").permitAll()
                        .requestMatchers(HttpMethod.GET,"api/v1/auth/refreshTokens").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        .requestMatchers("/api/v1/media/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").hasAnyRole( "USER","ADMIN")
                        .requestMatchers("/api/v1/media").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/v1/papers").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/papers/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/adviser_details/**").hasAnyRole("ADVISER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/adviser-assignments/**").hasAnyRole("ADMIN", "ADVISER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/api/v1/auth/login") do not // user Spring automatically redirects to Keycloak login page.
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(new OidcUserService()))
                                .successHandler((request, response, authentication) -> {
                                HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
                                SavedRequest savedRequest = requestCache.getRequest(request, response);
                                    if (savedRequest != null) {
                                        // Redirect to the URL user originally wanted
                                        String targetUrl = savedRequest.getRedirectUrl();
                                        response.sendRedirect(targetUrl);
                                    } else {
                                        // Default fallback if no original URL saved
                                        response.sendRedirect("http://localhost:3000");
                                    }
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
                            String keycloakLogoutUrl = "http://localhost:9090/realms/docuapi/protocol/openid-connect/logout";
                            // Redirect back to your backend endpoint after logout
                            String redirectAfterLogout = "http://localhost:8080/api/v1/auth/tokens";
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
