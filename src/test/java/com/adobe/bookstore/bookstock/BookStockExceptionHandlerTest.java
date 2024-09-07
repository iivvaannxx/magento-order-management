package com.adobe.bookstore.bookstock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.adobe.bookstore.bookstock.exceptions.NonExistentBookException;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Unit tests for the {@link BookStockExceptionHandler} class. */
class BookStockExceptionHandlerTest {

  /** The instance of {@link BookStockExceptionHandler} used in the tests. */
  private BookStockExceptionHandler bookStockExceptionHandler;

  /** The mocked instance of {@link Logger} used in the tests. */
  private Logger logger;

  /** Runs before each test. */
  @BeforeEach
  void setUp() {
    logger = mock(Logger.class);
    bookStockExceptionHandler = new BookStockExceptionHandler(logger);
  }

  /**
   * Tests that the {@link
   * BookStockExceptionHandler#handleNonExistantBookException(NonExistentBookException)} method is
   * annotated with the correct {@link ResponseStatus}.
   */
  @Test
  void handleNonExistantBookException_shouldHaveCorrectResponseStatus() {
    assertDoesNotThrow(
        () -> {
          Method method =
              BookStockExceptionHandler.class.getMethod(
                  "handleNonExistantBookException", NonExistentBookException.class);
          ResponseStatus annotation = method.getAnnotation(ResponseStatus.class);
          assertThat(annotation.value()).isEqualTo(HttpStatus.NOT_FOUND);
        });
  }

  /**
   * Tests that the {@link
   * BookStockExceptionHandler#handleNonExistantBookException(NonExistentBookException)} method
   * returns a map with the correct error information.
   */
  @Test
  void handleNonExistantBookException_shouldReturnCorrectErrorMap() {
    NonExistentBookException ex = new NonExistentBookException("12345-67890");
    Map<String, Object> result = bookStockExceptionHandler.handleNonExistantBookException(ex);

    assertThat(result)
        .isNotNull()
        .containsEntry("error", "Book does not exist")
        .containsEntry("bookId", "12345-67890");
  }

  /**
   * Tests that the {@link
   * BookStockExceptionHandler#handleNonExistantBookException(NonExistentBookException)} method logs
   * the error message correctly.
   */
  @Test
  void handleNonExistantBookException_shouldLogErrorMessage() {
    NonExistentBookException ex = new NonExistentBookException("12345-67890");
    bookStockExceptionHandler.handleNonExistantBookException(ex);

    verify(logger).error(ex.getMessage());
  }
}
