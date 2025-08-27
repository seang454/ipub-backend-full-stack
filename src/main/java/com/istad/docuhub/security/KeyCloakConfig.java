package com.istad.docuhub.security;


import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KeyCloakConfig {
    @Bean
    public Keycloak keyCloak() {
        return KeycloakBuilder
                .builder()
                .serverUrl("http://localhost:9090")
                .realm("docuapi")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin-cli")
                .clientSecret("o0oMFhSe9w7CiRWXXnaZI30ljw4Z3jzM")
                .build();
    }
}
