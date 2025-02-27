package com.example.expensemanager.security.jwt;

import java.security.Key;
import java.util.Date;

import com.example.expensemanager.service.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    public JwtUtils(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Value("${app.jwtSecret}") // Lấy JWT secret key từ application.properties/yml
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}") // Lấy JWT expiration time từ application.properties/yml
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) { // Tạo JWT token từ Authentication
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Set subject là username
                .setIssuedAt(new Date()) // Set thời điểm tạo token
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Set thời điểm hết hạn token
                .signWith(key(), SignatureAlgorithm.HS256) // Ký token bằng secret key và thuật toán HS256
                .compact();
    }

    private Key key() { // Tạo Key từ secret key string
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) { // Lấy username từ JWT token
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) { // Kiểm tra JWT token có hợp lệ không
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}