package com.adobe.bookstore.orders;

import com.adobe.bookstore.orders.exceptions.InsufficientStockException;
import com.adobe.bookstore.orders.exceptions.NonExistentOrderException;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Defines the exception handler for the {@link com.adobe.bookstore.orders.exceptions}. */
@RestControllerAdvice
public class OrderExceptionHandler {

  /** The logger instance for this class. */
  private final Logger logger;

  /**
   * Creates a new instance of the {@link OrderExceptionHandler} class.
   *
   * @param logger An instance of {@link Logger}.
   */
  @Autowired
  public OrderExceptionHandler(Logger logger) {
    this.logger = logger;
  }

  /**
   * Handles the {@link InsufficientStockException} and returns a 409 Conflict response.
   *
   * @param ex The {@link InsufficientStockException} to handle.
   */
  @ExceptionHandler(InsufficientStockException.class)
  @ResponseStatus(
      // We return a 409 Conflict to inform of a conflict with the current state of the resource.
      HttpStatus.CONFLICT)
  public Map<String, Object> handleInsufficientStockException(InsufficientStockException ex) {

    logger.error(ex.getMessage());
    return Map.of(
        "error", "Not enough stock for book",
        "bookId", ex.getBookId(),
        "requestedQuantity", ex.getRequestedQuantity(),
        "availableQuantity", ex.getAvailableQuantity());
  }

  /**
   * Handles the {@link NonExistentOrderException} and returns a 404 Not Found response.
   *
   * @param ex The {@link NonExistentOrderException} to handle.
   */
  @ExceptionHandler(NonExistentOrderException.class)
  @ResponseStatus(
      // We return a 404 Not Found to inform that the resource does not exist.
      HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNonExistantOrderException(NonExistentOrderException ex) {

    logger.error(ex.getMessage());
    return Map.of("error", "Order does not exist", "orderId", ex.getOrderId());
  }
}
