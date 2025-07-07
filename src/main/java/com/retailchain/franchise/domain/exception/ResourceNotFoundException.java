package com.retailchain.franchise.domain.exception;

public class ResourceNotFoundException extends BusinessException {
    private static final long serialVersionUID = 1L;
    
    public ResourceNotFoundException(String resource, Long id) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s with id %d not found", resource, id));
    }
    
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
}