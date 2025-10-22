//package com.istad.docuhub.feature.webSocket;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//
//public class JwtUtil {
//
//    // Use your Keycloak realm's secret or public key
//    private static final String SECRET = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArwxmO6ckR4LWRVVedAG7G+6hQElM5YFcsSNNGDwctTUeUDQVR6SUIncwFE2R0fojOSgbH+bVvct+mx5ZO1okVesX3d0yoo3FQ7aa4vFHWe+1yqbZj/Q5S+JhvxzbbErOdKs24Su38wrJ0KGqb6xtIWTk8AtO1AxdzMfnLWmjq8vJUfObZjM5NXYw4Li+hZUHk4Sx2IJlamXhADV5ly0mk+aq6lw9crbrenXOt1jYxR0TxNN61gNAvz/iNBhqslqRWmZO7RvQ0ePTdiXb40YUal5Ya3b3VuLDG0nr8jGNdI1pIrLk18jtMKwm8682j58eWkwIDE16N4/kMYYew56RhwIDAQAB"; // 256-bit key for HS256
//
//    private static Key getSigningKey() {
//        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
//    }
//
//    public static boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey())
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public static String getUsernameFromToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.getSubject(); // Usually the username/email
//    }
//}
//
