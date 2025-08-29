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
                .serverUrl("https://keycloak.docuhub.me")
                .realm("docuapi")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin-cli")
                .clientSecret("i9C5KGtVXMuWwrkEFIq4esJKUMMomxPl")
                .build();
    }
}
