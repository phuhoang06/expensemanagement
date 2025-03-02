package com.example.expensemanager.payload.request;


import jakarta.validation.constraints.NotBlank;




// LoginRequest.java
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


}

