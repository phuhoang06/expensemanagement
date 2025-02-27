package com.example.expensemanager.security.keys;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Base64;
import java.security.Key;

@Configuration
public class JwtSecretKeyConfig {

    @Bean
    public String jwtSecret() {
        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256); // Tạo Key mạnh cho HS256
        return Base64.getEncoder().encodeToString(key.getEncoded()); // Mã hóa base64 và trả về
    }
}