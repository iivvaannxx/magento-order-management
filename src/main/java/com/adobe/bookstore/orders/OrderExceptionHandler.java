package com.adobe.bookstore.orders;

import com.adobe.bookstore.orders.exceptions.InsufficientStockException;
import com.adobe.bookstore.orders.exceptions.NonExistentOrderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/** Defines the exception handler for the {@link com.adobe.bookstore.orders.exceptions}. */
@RestControllerAdvice
public class OrderExceptionHandler {

  /** The logger instance for this class. */
  private static final Logger logger = LoggerFactory.getLogger(OrderExceptionHandler.class);

  /**
   * Handles the {@link InsufficientStockException} and returns a 409 Conflict response.
   *
   * @param ex The {@link InsufficientStockException} to handle.
   */
  @ExceptionHandler(InsufficientStockException.class)
  @ResponseStatus(
      HttpStatus
          .CONFLICT) // We return a 409 Conflict to emphasize that the request conflicts with the
  // current state of the resource.
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
      HttpStatus.NOT_FOUND) // We return a 404 Not Found to inform that the resource does not exist.
  public Map<String, Object> handleNonExistantOrderException(NonExistentOrderException ex) {

    logger.error(ex.getMessage());
    return Map.of("error", "Order does not exist", "orderId", ex.getOrderId());
  }
}
