package com.example.ppttemplateapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/ppt/**").permitAll()  // PPT 관련 엔드포인트는 인증 없이 허용
                .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔 접근 허용
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/ppt/**")  // PPT 엔드포인트에서 CSRF 비활성화
                .ignoringRequestMatchers("/h2-console/**")  // H2 콘솔에서 CSRF 비활성화
            )
            .headers(headers -> headers
                .frameOptions().disable()  // H2 콘솔을 위해 iframe 허용
            );

        return http.build();
    }
} 