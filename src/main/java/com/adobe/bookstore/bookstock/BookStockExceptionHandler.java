package com.adobe.bookstore.bookstock;

import com.adobe.bookstore.bookstock.exceptions.NonExistantBookException;
import com.adobe.bookstore.orders.OrderExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/** Defines the exception handler for the {@link com.adobe.bookstore.bookstock.exceptions}. */
@RestControllerAdvice
public class BookStockExceptionHandler {
    
    /** The logger instance for this class. */
    private static final Logger logger = LoggerFactory.getLogger(OrderExceptionHandler.class);
    
    /**
     * Handles the {@link NonExistantBookException} and returns a 404 Not Found response.
     * @param ex The {@link NonExistantBookException} to handle.
     */
    @ExceptionHandler(NonExistantBookException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // We return a 404 Not Found to inform that the resource does not exist.
    public Map<String, Object> handleNonExistantBookException(NonExistantBookException ex) {
        
        logger.error(ex.getMessage());
        return Map.of(
            "error", ex.getMessage(),
            "bookId", ex.getBookId()
        );
    }
    
}
