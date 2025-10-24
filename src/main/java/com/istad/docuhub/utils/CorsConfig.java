//package com.istad.docuhub.utils;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig {
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
////                registry.addMapping("/api/**")
//                registry.addMapping("**")
////                        .allowedOrigins("http://localhost:8080", "http://127.0.0.1:5500") // Add your frontend URL
////                        .allowedMethods("GET", "POST", "PUT", "DELETE")
////                        .allowedHeaders("*")
////                        .allowCredentials(true);
//
//                        .allowedOriginPatterns("*")
//                        .allowedMethods("*")
//                        .allowCredentials(true);
//            }
//        };
//    }
//}