package com.adobe.bookstore;

import java.util.HashMap;
import java.util.Map;

import com.adobe.bookstore.orders.OrderExceptionHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Defines the exception handler for the global exceptions. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** The logger instance for this class. */
  private final Logger logger;

  /**
   * Creates a new instance of the {@link GlobalExceptionHandler} class.
   *
   * @param logger An instance of {@link Logger}.
   */
  @Autowired
  public GlobalExceptionHandler(Logger logger) {
    this.logger = logger;
  }

  /**
   * Handles the {@link MethodArgumentNotValidException} and returns a 400 Bad Request response.
   *
   * @param ex The {@link MethodArgumentNotValidException} to handle.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(
      HttpStatus.BAD_REQUEST) // We return a 400 Bad Request to inform that the request is invalid.
  public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {

    // Collect all the errors in a map of field names and error messages.
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();

              errors.put(fieldName, errorMessage);
            });

    logger.error("Error during validation: {}", errors);
    return Map.of("error", "The request is invalid or malformed", "validationErrors", errors);
  }
}
