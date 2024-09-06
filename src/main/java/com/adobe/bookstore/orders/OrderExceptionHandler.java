package com.adobe.bookstore.orders;

import com.adobe.bookstore.orders.exceptions.InsufficientStockException;
import com.adobe.bookstore.orders.exceptions.NonExistantOrderException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Defines the exception handler for the {@link com.adobe.bookstore.orders.exceptions}. */
@ControllerAdvice
public class OrderExceptionHandler {
    
    /**
     * Handles the {@link InsufficientStockException} and returns a 409 Conflict response.
     * @param ex The {@link InsufficientStockException} to handle.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStockException(InsufficientStockException ex) {
        // We return a 409 Conflict to emphasize that the request conflicts with the current state of the resource.
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    
    /**
     * Handles the {@link NonExistantOrderException} and returns a 404 Not Found response.
     * @param ex The {@link NonExistantOrderException} to handle.
     */
    @ExceptionHandler(NonExistantOrderException.class)
    public ResponseEntity<String> handleNonExistantOrderException(
        NonExistantOrderException ex) {
        // We return a 404 Not Found to inform that the resource does not exist.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
