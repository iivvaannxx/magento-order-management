package com.adobe.bookstore.bookstock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adobe.bookstore.bookstock.exceptions.NonExistentBookException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Unit tests for the {@link BookStockResource} class. */
public class BookStockResourceTest {

  /** The instance of {@link BookStockResource} used in the tests. */
  private BookStockResource bookStockResource;

  /** The instance of {@link BookStockService} used in the tests. */
  private BookStockService bookStockService;

  /** Runs before each test. */
  @BeforeEach
  void setUp() {
    bookStockService = mock(BookStockService.class);
    bookStockResource = new BookStockResource(bookStockService);
  }

  /**
   * Tests that the {@link BookStockResource#getStockById(String)} method works correctly if the
   * book exists.
   */
  @Test
  void getStockById_whenBookExists_shouldReturnBookStock() {

    String bookId = "12345-67890";
    BookStock expectedBookStock = new BookStock(bookId, "Some Book", 10);
    when(bookStockService.getBookById(bookId)).thenReturn(expectedBookStock);

    ResponseEntity<BookStock> result = bookStockResource.getStockById(bookId);
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isEqualTo(expectedBookStock);

    verify(bookStockService, times(1)).getBookById(bookId);
  }

  /**
   * Tests that the {@link BookStockResource#getStockById(String)} throws an exception if the book
   * does not exist.
   */
  @Test
  void getStockById_whenBookDoesNotExist_shouldPropagateException() {

    String bookId = "12345-67890";
    when(bookStockService.getBookById(bookId)).thenThrow(new NonExistentBookException(bookId));

    // We are unit testing the controller in isolation.
    // This means that the exception handler is not in the context.
    // Then we don't expect a 404 response, but the exception to be propagated.
    assertThatThrownBy(() -> bookStockResource.getStockById(bookId));
    verify(bookStockService, times(1)).getBookById(bookId);
  }
}
