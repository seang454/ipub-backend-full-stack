package com.istad.docuhub.security;


import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KeyCloakConfig {

    @Value("{}")

    @Bean
    public Keycloak keyCloak() {
        return KeycloakBuilder
                .builder()
                .serverUrl("http://localhost:9090")
                .realm("docuapi")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin-cli")
                .clientSecret("Tga7gc0Me3FG88eAj3SPaItNzqgWuZOX")
                .build();
    }
}
