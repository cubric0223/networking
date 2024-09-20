package com.networkingProject.rightNow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://beancp.com:*")  // 포트와 관계없이 도메인 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메소드
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With")  // 허용할 헤더
                .allowCredentials(true)  // 쿠키를 포함한 요청 허용
                .maxAge(3600);  // CORS 설정 캐시 시간 (1시간)
    }
}
