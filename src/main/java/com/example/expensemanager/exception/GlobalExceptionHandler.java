package com.example.expensemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED.value());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // Bạn có thể thêm các @ExceptionHandler khác để xử lý các exception khác nếu cần.

     @ExceptionHandler(Exception.class) // Handle all other exceptions
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "An unexpected error occurred."); // Generic message
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", ex.getMessage()); // Include the original error (for debugging)

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}