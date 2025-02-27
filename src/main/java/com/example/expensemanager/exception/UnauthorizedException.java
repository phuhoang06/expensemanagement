package com.example.expensemanager.exception;
public class UnauthorizedException  extends  RuntimeException{
    public UnauthorizedException(String message){
        super(message);
    }
}