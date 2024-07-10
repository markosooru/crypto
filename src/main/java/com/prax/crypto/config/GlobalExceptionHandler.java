package com.prax.crypto.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        var error = new HashMap<String, String>();
        var rootCause = getRootCause(ex);
        var message = rootCause.getMessage();

        if (message != null && message.contains("duplicate key value violates unique constraint") && message.contains("app_user_email_key")) {
            var duplicateEmail = extractDuplicateEmail(message);
            error.put("error", "Employee with email: " + duplicateEmail + " already exists.");
        } else {
            error.put("error", "A data integrity violation occurred.");
        }
        return error;
    }

    private String extractDuplicateEmail(String message) {
        var pattern = Pattern.compile("\\(email\\)=\\((.*?)\\)");
        var matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "unknown";
    }

    private Throwable getRootCause(Throwable throwable) {
        var cause = new Throwable();
        var result = throwable;

        while ((cause = result.getCause()) != null && result != cause) {
            result = cause;
        }
        return result;
    }
}