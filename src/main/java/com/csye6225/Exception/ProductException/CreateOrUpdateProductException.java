package com.csye6225.Exception.ProductException;

public class CreateOrUpdateProductException extends RuntimeException {
    public CreateOrUpdateProductException(String message){
        super(message);
    }
}
