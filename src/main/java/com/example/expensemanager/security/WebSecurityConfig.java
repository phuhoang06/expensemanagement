// KHÔNG AN TOÀN - CHỈ DÙNG CHO MỤC ĐÍCH THỬ NGHIỆM
package com.example.expensemanager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Tắt CSRF protection (KHÔNG an toàn)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // Cho phép tất cả các request
                        .anyRequest().authenticated() // Cái này cũng không cần thiết nếu đã permitAll ở trên
                );
        return http.build();
    }
}