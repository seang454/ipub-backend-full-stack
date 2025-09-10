package com.istad.docuhub.feature.user.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KeycloakLoginController {

    @GetMapping("/api/v1/auth/keycloak/login")
    public String redirectToKeycloak() {
        // Redirect browser to Spring Security OAuth2 login URL
        return "redirect:/oauth2/authorization/keycloak";
    }
}
