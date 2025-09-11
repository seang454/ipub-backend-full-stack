package com.istad.docuhub.utils;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

// bellow service all use catch current user, id, email username and role for validation
@Configuration
public class QuickService {
    public CurrentUserV2 currentUserInfor() {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return CurrentUserV2.builder()
                .id(authentication.getToken().getClaimAsString("sub"))
                .uuid(authentication.getToken().getClaimAsString("uuid")) // custom claim
                .email(authentication.getToken().getClaimAsString("email")) // optional
                .roles(authentication.getToken().getClaimAsStringList("roles")) // roles claim
                .build();
    }
}
