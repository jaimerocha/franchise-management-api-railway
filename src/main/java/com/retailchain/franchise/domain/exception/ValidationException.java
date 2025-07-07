package com.retailchain.franchise.domain.exception;

import java.util.Map;

public class ValidationException extends BusinessException {
    private static final long serialVersionUID = 1L;
    private final Map<String, String> errors;
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.errors = Map.of();
    }
    
    public ValidationException(Map<String, String> errors) {
        super("VALIDATION_ERROR", "Validation failed");
        this.errors = errors;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
}