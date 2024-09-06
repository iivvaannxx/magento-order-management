package com.adobe.bookstore.bookstock;

import com.adobe.bookstore.bookstock.exceptions.NonExistentBookException;
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
     * Handles the {@link NonExistentBookException} and returns a 404 Not Found response.
     * @param ex The {@link NonExistentBookException} to handle.
     */
    @ExceptionHandler(NonExistentBookException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // We return a 404 Not Found to inform that the resource does not exist.
    public Map<String, Object> handleNonExistantBookException(NonExistentBookException ex) {
        
        logger.error(ex.getMessage());
        return Map.of(
            "error", "Book does not exist",
            "bookId", ex.getBookId()
        );
    }
    
}
