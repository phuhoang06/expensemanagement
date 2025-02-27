package com.example.expensemanager.security.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.expensemanager.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request); // Lấy JWT từ header
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) { // Kiểm tra JWT có hợp lệ
                String username = jwtUtils.getUserNameFromJwtToken(jwt); // Lấy username từ JWT

                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Lấy UserDetails từ username
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities()); // Tạo Authentication object
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Set request details

                SecurityContextHolder.getContext().setAuthentication(authentication); // Set Authentication vào Security Context
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response); // Tiếp tục chuỗi filter
    }

    private String parseJwt(HttpServletRequest request) { // Lấy JWT từ header Authorization
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) { // Kiểm tra header có Authorization và bắt đầu bằng "Bearer "
            return headerAuth.substring(7); // Trả về token sau "Bearer "
        }

        return null;
    }
}