package com.adobe.bookstore;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
