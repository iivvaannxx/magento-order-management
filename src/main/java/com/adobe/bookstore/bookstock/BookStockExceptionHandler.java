package com.adobe.bookstore.bookstock;

import com.adobe.bookstore.bookstock.exceptions.NonExistantBookException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Defines the exception handler for the {@link com.adobe.bookstore.bookstock.exceptions}. */
@ControllerAdvice
public class BookStockExceptionHandler {
    
    /**
     * Handles the {@link NonExistantBookException} and returns a 404 Not Found response.
     * @param ex The {@link NonExistantBookException} to handle.
     */
    @ExceptionHandler(NonExistantBookException.class)
    public ResponseEntity<String> handleNonExistantBookException(NonExistantBookException ex) {
        // We return a 404 Not Found to inform that the resource does not exist.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
