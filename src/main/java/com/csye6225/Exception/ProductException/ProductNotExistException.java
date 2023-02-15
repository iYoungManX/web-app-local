package com.csye6225.Exception.ProductException;

public class ProductNotExistException extends RuntimeException {
    public ProductNotExistException(String message){
        super(message);
    }
}
