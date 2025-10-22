package com.istad.docuhub.feature.webSocket;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSecurity
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtDecoder jwtDecoder;
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String token = accessor.getFirstNativeHeader("Authorization");

                    // Fallback: check query parameter
                    if (token == null) {
                        Object accessTokenAttr = accessor.getSessionAttributes().get("access_token");
                        if (accessTokenAttr != null) {
                            token = accessTokenAttr.toString();
                        }
                    }

                    if (token == null) {
                        throw new IllegalArgumentException("No valid JWT token found");
                    }

                    // Remove Bearer prefix if present
                    if (token.startsWith("Bearer ")) {
                        token = token.substring(7);
                    }

                    try {
                        Jwt jwt = jwtDecoder.decode(token);
                        String userUuid = jwt.getClaimAsString("sub");
                        accessor.setUser(new StompPrincipal(userUuid));
                        System.out.println("WebSocket connected user UUID: " + userUuid);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid JWT token", e);
                    }
                }

                return message;
            }
        });
    }

}

