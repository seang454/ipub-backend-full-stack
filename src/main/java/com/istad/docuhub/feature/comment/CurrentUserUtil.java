package com.istad.docuhub.feature.comment;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class CurrentUserUtil {

    public Integer getCurrentUserId() {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getToken() == null) {
            throw new RuntimeException("No authenticated user found");
        }

        String sub = authentication.getToken().getClaimAsString("sub");
        if (sub == null) {
            throw new RuntimeException("User ID (sub) not found in token");
        }

        return Integer.valueOf(sub); // Convert String to Integer
    }

    /*
    ---- How to Use ----
    --->>   private CurrentUserUtil currentUserUtil;
    --->>   Integer userId = currentUserUtil.getCurrentUserId();
    * */

}
