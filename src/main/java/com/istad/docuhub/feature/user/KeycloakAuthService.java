package com.istad.docuhub.feature.user;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KeycloakAuthService {

    private final String KEYCLOAK_URL = "https://keycloak.docuhub.me/realms/docuapi/protocol/openid-connect/token";
    private final String CLIENT_ID = "docuhub-client";
    private final String CLIENT_SECRET = "2xvFvjFZoqQVFthEae5URLvTQnuu9E69"; // optional if public client

    public Map<String, Object> login(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", CLIENT_ID);
        if (CLIENT_SECRET != null && !CLIENT_SECRET.isEmpty()) {
            body.add("client_secret", CLIENT_SECRET);
        }
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(KEYCLOAK_URL, request, Map.class);

        // Return the token map directly
        return response.getBody();
    }
}

