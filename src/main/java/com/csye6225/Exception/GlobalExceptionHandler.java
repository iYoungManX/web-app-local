package com.csye6225.Exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.csye6225.Util.ErrorMessage;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RepeatEmailException.class)
    public ResponseEntity handleRepeatEmailException(RepeatEmailException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(ErrorMessage.INVALID_EMAIL);
    }

    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity handleInvalidUpdateException(InvalidUpdateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity handleJWTDecodeException(JWTDecodeException e) {
        return ResponseEntity.badRequest().body(ErrorMessage.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthrizedException.class)
    public ResponseEntity handleUnauthorizedException(UnauthrizedException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }


    @ExceptionHandler(ChangeOthersInfoException.class)
    public ResponseEntity handleUnauthorizedException(ChangeOthersInfoException e){
        return ResponseEntity.status(HttpStatusCode.valueOf(403))
        .body(e.getMessage());
    }


    @ExceptionHandler(GetOthersInfoException.class)
    public ResponseEntity handleUnauthorizedException(GetOthersInfoException e){
        return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
    }
}

