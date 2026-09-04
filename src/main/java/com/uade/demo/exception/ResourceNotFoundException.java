package com.uade.demo.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Long id) {
        super("No se encontró el usuario con id: " + id);
    }

    // Constructor: Acepta cualquier String como mensaje.
    public ResourceNotFoundException(String message) {
        super(message);
    }    
}
