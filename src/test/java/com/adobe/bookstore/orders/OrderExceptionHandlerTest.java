package com.adobe.bookstore.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.adobe.bookstore.orders.exceptions.InsufficientStockException;
import com.adobe.bookstore.orders.exceptions.NonExistentOrderException;
import java.lang.reflect.Method;
import java.util.Map;

import com.adobe.bookstore.orders.exceptions.OrderAlreadyContainsBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Unit tests for the {@link OrderExceptionHandler} class. */
class OrderExceptionHandlerTest {

  /** The instance of {@link OrderExceptionHandler} used in the tests. */
  private OrderExceptionHandler orderExceptionHandler;

  /** The mocked instance of {@link Logger} used in the tests. */
  private Logger logger;

  /** Runs before each test. */
  @BeforeEach
  void setUp() {
    logger = mock(Logger.class);
    orderExceptionHandler = new OrderExceptionHandler(logger);
  }

  /**
   * Tests that the {@link
   * OrderExceptionHandler#handleNonExistentOrderException(NonExistentOrderException)} method is
   * annotated with the correct {@link ResponseStatus}.
   */
  @Test
  void handleNonExistentOrderException_shouldHaveCorrectResponseStatus() {
    assertDoesNotThrow(
        () -> {
          Method method =
              OrderExceptionHandler.class.getMethod(
                  "handleNonExistentOrderException", NonExistentOrderException.class);

          ResponseStatus annotation = method.getAnnotation(ResponseStatus.class);
          assertThat(annotation.value()).isEqualTo(HttpStatus.NOT_FOUND);
        });
  }

  /**
   * Tests that the {@link
   * OrderExceptionHandler#handleNonExistentOrderException(NonExistentOrderException)} method
   * returns a map with the correct error information.
   */
  @Test
  void handleNonExistentBookException_shouldReturnCorrectErrorMap() {
    NonExistentOrderException ex = new NonExistentOrderException("11111-22222");
    Map<String, Object> result = orderExceptionHandler.handleNonExistentOrderException(ex);

    assertThat(result)
        .isNotNull()
        .containsEntry("error", "Order does not exist")
        .containsEntry("orderId", "11111-22222");
  }

  /**
   * Tests that the {@link
   * OrderExceptionHandler#handleNonExistentOrderException(NonExistentOrderException)} method logs
   * the error message correctly.
   */
  @Test
  void handleNonExistentBookException_shouldLogErrorMessage() {
    NonExistentOrderException ex = new NonExistentOrderException("11111-22222");
    orderExceptionHandler.handleNonExistentOrderException(ex);

    verify(logger).error(ex.getMessage());
  }

  /**
   * Tests that the {@link
   * OrderExceptionHandler#handleOrderAlreadyContainsBookException(OrderAlreadyContainsBook)} method
   * is annotated with the correct {@link ResponseStatus}.
   */
  @Test
  void handlerOrderAlreadyContainsBook_shouldHaveCorrectResponseStatus() {
    assertDoesNotThrow(
        () -> {
          Method method =
              OrderExceptionHandler.class.getMethod(
                  "handleOrderAlreadyContainsBookException", OrderAlreadyContainsBook.class);

          ResponseStatus annotation = method.getAnnotation(ResponseStatus.class);
          assertThat(annotation.value()).isEqualTo(HttpStatus.BAD_REQUEST);
        });
  }

  /**
   * Tests that the {@link
   * OrderExceptionHandler#handleOrderAlreadyContainsBookException(OrderAlreadyContainsBook)} method
   * returns a map with the correct error information.
   */
  @Test
  void handleOrderAlreadyContainsBook_shouldReturnCorrectErrorMap() {
    OrderAlreadyContainsBook ex = new OrderAlreadyContainsBook("12345-67890");
    Map<String, Object> result = orderExceptionHandler.handleOrderAlreadyContainsBookException(ex);

    assertThat(result)
        .isNotNull()
        .containsEntry("error", "Order already contains book")
        .containsEntry("bookId", "12345-67890");
  }

  /**
   * Tests that the {@link
   * OrderExceptionHandler#handleOrderAlreadyContainsBookException(OrderAlreadyContainsBook)} method
   * logs the error message correctly.
   */
  @Test
  void handleOrderAlreadyContainsBook_shouldLogErrorMessage() {
    OrderAlreadyContainsBook ex = new OrderAlreadyContainsBook("12345-67890");
    orderExceptionHandler.handleOrderAlreadyContainsBookException(ex);

    verify(logger).error(ex.getMessage());
  }

  /**
   * Tests that the {@link
   * OrderExceptionHandler#handleInsufficientStockException(InsufficientStockException)} method is
   * annotated with the correct {@link ResponseStatus}.
   */
  @Test
  void handleInsufficientStockException_shouldHaveCorrectResponseStatus() {
    assertDoesNotThrow(
        () -> {
          Method method =
              OrderExceptionHandler.class.getMethod(
                  "handleInsufficientStockException", InsufficientStockException.class);

          ResponseStatus annotation = method.getAnnotation(ResponseStatus.class);
          assertThat(annotation.value()).isEqualTo(HttpStatus.CONFLICT);
        });
  }

  /**
   * Tests that the {@link
   * OrderExceptionHandler#handleInsufficientStockException(InsufficientStockException)} method
   * returns a map with the correct error information.
   */
  @Test
  void handleInsufficientStockException_shouldReturnCorrectErrorMap() {
    InsufficientStockException ex = new InsufficientStockException("12345-67890", 10, 5);
    Map<String, Object> result = orderExceptionHandler.handleInsufficientStockException(ex);

    assertThat(result)
        .isNotNull()
        .containsEntry("error", "Not enough stock for book")
        .containsEntry("bookId", "12345-67890")
        .containsEntry("requestedQuantity", 10)
        .containsEntry("availableQuantity", 5);
  }

  /**
   * Tests that the {@link
   * OrderExceptionHandler#handleInsufficientStockException(InsufficientStockException)} method logs
   * the error message correctly.
   */
  @Test
  void handleInsufficientStockException_shouldLogErrorMessage() {
    InsufficientStockException ex = new InsufficientStockException("12345-67890", 10, 5);
    orderExceptionHandler.handleInsufficientStockException(ex);

    verify(logger).error(ex.getMessage());
  }
}
