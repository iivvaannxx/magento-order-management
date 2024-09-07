package com.adobe.bookstore.bookstock;

import com.adobe.bookstore.bookstock.exceptions.NonExistentBookException;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Defines the exception handler for the {@link com.adobe.bookstore.bookstock.exceptions}. */
@RestControllerAdvice
public class BookStockExceptionHandler {

  /** The logger instance for this class. */
  private final Logger logger;

  /**
   * Creates a new instance of the {@link BookStockExceptionHandler} class.
   *
   * @param logger An instance of {@link Logger}.
   */
  @Autowired
  public BookStockExceptionHandler(Logger logger) {
    this.logger = logger;
  }

  /**
   * Handles the {@link NonExistentBookException} and returns a 404 Not Found response.
   *
   * @param ex The {@link NonExistentBookException} to handle.
   */
  @ExceptionHandler(NonExistentBookException.class)
  @ResponseStatus(
      // We return a 404 Not Found to inform that the resource does not exist.
      HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNonExistantBookException(NonExistentBookException ex) {

    logger.error(ex.getMessage());
    return Map.of("error", "Book does not exist", "bookId", ex.getBookId());
  }
}
