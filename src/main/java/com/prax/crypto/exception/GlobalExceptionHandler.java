package com.prax.crypto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CryptoAppException.class)
    public ResponseEntity<Map<String, String>> handleCryptoAppException(CryptoAppException ex) {
        var error = new HashMap<String, String>();
        error.put("error", ex.getMessage());

        HttpStatus status;
        switch (ex) {
            case EmailAlreadyExistsException exc -> status = HttpStatus.CONFLICT;
            case NotAuthenticatedException exc -> status = HttpStatus.UNAUTHORIZED;
            case PortfolioNotFoundException exc -> status = HttpStatus.NOT_FOUND;
            case UserNotFoundException exc -> status = HttpStatus.NOT_FOUND;
            default -> status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(error, status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}