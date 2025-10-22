package com.istad.docuhub.feature.webSocket;

import java.security.Principal;

/**
 * Custom Principal to store the authenticated user UUID
 * in WebSocket STOMP sessions.
 */
public class StompPrincipal implements Principal {

    private final String name; // usually user UUID

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}

