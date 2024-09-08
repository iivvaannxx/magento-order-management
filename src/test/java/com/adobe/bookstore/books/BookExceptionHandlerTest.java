package com.adobe.bookstore.books;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.adobe.bookstore.books.exceptions.NonExistentBookException;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Unit tests for the {@link BookExceptionHandler} class. */
class BookExceptionHandlerTest {

  /** The instance of {@link BookExceptionHandler} used in the tests. */
  private BookExceptionHandler bookExceptionHandler;

  /** The mocked instance of {@link Logger} used in the tests. */
  private Logger logger;

  /** Runs before each test. */
  @BeforeEach
  void setUp() {
    logger = mock(Logger.class);
    bookExceptionHandler = new BookExceptionHandler(logger);
  }

  /**
   * Tests that the {@link
   * BookExceptionHandler#handleNonExistentBookException(NonExistentBookException)} method is
   * annotated with the correct {@link ResponseStatus}.
   */
  @Test
  void handleNonExistentBookException_shouldHaveCorrectResponseStatus() {
    assertDoesNotThrow(
        () -> {
          Method method =
              BookExceptionHandler.class.getMethod(
                  "handleNonExistentBookException", NonExistentBookException.class);
          ResponseStatus annotation = method.getAnnotation(ResponseStatus.class);
          assertThat(annotation.value()).isEqualTo(HttpStatus.NOT_FOUND);
        });
  }

  /**
   * Tests that the {@link
   * BookExceptionHandler#handleNonExistentBookException(NonExistentBookException)} method
   * returns a map with the correct error information.
   */
  @Test
  void handleNonExistentBookException_shouldReturnCorrectErrorMap() {
    NonExistentBookException ex = new NonExistentBookException("12345-67890");
    Map<String, Object> result = bookExceptionHandler.handleNonExistentBookException(ex);

    assertThat(result)
        .isNotNull()
        .containsEntry("error", "Book does not exist")
        .containsEntry("bookId", "12345-67890");
  }

  /**
   * Tests that the {@link
   * BookExceptionHandler#handleNonExistentBookException(NonExistentBookException)} method logs
   * the error message correctly.
   */
  @Test
  void handleNonExistantBookException_shouldLogErrorMessage() {
    NonExistentBookException ex = new NonExistentBookException("12345-67890");
    bookExceptionHandler.handleNonExistentBookException(ex);

    verify(logger).error(ex.getMessage());
  }
}
