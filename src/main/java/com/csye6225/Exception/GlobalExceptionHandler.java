package com.csye6225.Exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.csye6225.Exception.ProductException.CreateOrUpdateProductException;
import com.csye6225.Exception.ProductException.NoContentException;
import com.csye6225.Exception.ProductException.ProductNotExistException;
import com.csye6225.Exception.UserException.*;
import com.csye6225.Util.ErrorMessage;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RepeatEmailException.class)
    public ResponseEntity<String> handleRepeatEmailException(RepeatEmailException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(ErrorMessage.INVALID_EMAIL);
    }

    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<String> handleInvalidUpdateException(InvalidUpdateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<String> handleJWTDecodeException(JWTDecodeException e) {
        return ResponseEntity.badRequest().body(ErrorMessage.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthrizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthrizedException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }


    @ExceptionHandler(ChangeOthersInfoException.class)
    public ResponseEntity<String> handleUnauthorizedException(ChangeOthersInfoException e){
        return ResponseEntity.status(HttpStatusCode.valueOf(403))
        .body(e.getMessage());
    }


    @ExceptionHandler(GetOthersInfoException.class)
    public ResponseEntity<String> handleUnauthorizedException(GetOthersInfoException e){
        return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
    }

    @ExceptionHandler(ProductNotExistException.class)
    public ResponseEntity<String> handleProductNotExistException(ProductNotExistException e){
        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(e.getMessage());
    }

    @ExceptionHandler(CreateOrUpdateProductException.class)
    public ResponseEntity<String> handleCreateOrUpdateProductException(CreateOrUpdateProductException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }


    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<String> handleNoContentException(NoContentException e){
            return ResponseEntity.status(204).body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return ResponseEntity.badRequest().body(ErrorMessage.QUANTITY_ERROR);
    }

}

