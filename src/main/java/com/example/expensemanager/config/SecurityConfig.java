package com.example.expensemanager.config;

import com.example.expensemanager.security.jwt.JwtRequestFilter; // Import JwtRequestFilter
import com.example.expensemanager.security.jwt.JwtUtils; // Import JwtUtils
import com.example.expensemanager.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Import SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Import UsernamePasswordAuthenticationFilter

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter; // Inject JwtRequestFilter

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable()) // Tắt CSRF
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set session stateless (cho JWT)
                )
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/api/auth/**").permitAll() // Cho phép /api/auth/** (signup, login) không cần xác thực
                        .requestMatchers("/api/test/**").permitAll() // Cho phép /api/test/**
                        .requestMatchers("/api/categories/**").authenticated() // Yêu cầu xác thực cho /api/categories/**
                        .requestMatchers("/api/transactions/**").authenticated() // Yêu cầu xác thực cho /api/transactions/**
                        .requestMatchers("/api/budgets/**").authenticated()    // Yêu cầu xác thực cho /api/budgets/**
                        .requestMatchers("/api/reminders/**").authenticated()  // Yêu cầu xác thực cho /api/reminders/**
                        .requestMatchers("/api/bankaccounts/**").authenticated() // Yêu cầu xác thực cho /api/bankaccounts/**
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")     // Yêu cầu vai trò ADMIN cho /api/admin/**
                        .anyRequest().permitAll() // Cho phép tất cả các request khác
                )
                //.httpBasic(withDefaults()) // **Bỏ httpBasic()**
                .authenticationProvider(authenticationProvider()) // Sử dụng Authentication Provider

                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Thêm JwtRequestFilter trước UsernamePasswordAuthenticationFilter

        return http.build();
    }
}