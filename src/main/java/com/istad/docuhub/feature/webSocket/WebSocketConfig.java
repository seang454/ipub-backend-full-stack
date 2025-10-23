package com.istad.docuhub.feature.webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Use topics only; we won't use /user/ routing anymore.
        config.enableSimpleBroker("/topic");
        // Prefix for messages sent from clients to @MessageMapping handlers
        config.setApplicationDestinationPrefixes("/app");
        // (Optional) You can keep user prefix if you use it elsewhere, but not needed here:
        // config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                // Allow your frontend origin(s)
                .setAllowedOriginPatterns("http://localhost:3000","http://localhost:3001", "http://localhost:5173", "*")
                .withSockJS();
    }
}

