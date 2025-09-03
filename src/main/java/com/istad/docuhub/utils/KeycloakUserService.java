package com.istad.docuhub.utils;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakUserDto getUserById(String userId) {
        UserRepresentation userRep = keycloak
                .realm(realm)
                .users()
                .get(userId)
                .toRepresentation();

        return new KeycloakUserDto(
                userRep.getId(),
                userRep.getUsername(),
                userRep.getEmail()
        );
    }
}

