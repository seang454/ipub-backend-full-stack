//package com.istad.docuhub.feature.webSocket;
//
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServletServerHttpRequest;
//
//import jakarta.servlet.http.HttpServletRequest;
//import java.security.Principal;
//import java.util.Map;
//import java.util.Collections;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//
//public class CustomHandshakeHandler extends DefaultHandshakeHandler {
//
//    @Override
//    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
//                                      Map<String, Object> attributes) {
//
//        if (!(request instanceof ServletServerHttpRequest)) return null;
//        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
//
//        // Extract token from query param
//        String token = servletRequest.getParameter("access_token");
//        if (token != null && JwtUtil.validateToken(token)) {
//            String username = JwtUtil.getUsernameFromToken(token);
//            return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
//        }
//        return null;
//    }
//}
