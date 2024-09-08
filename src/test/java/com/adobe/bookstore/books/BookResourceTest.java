package com.adobe.bookstore.books;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adobe.bookstore.books.exceptions.NonExistentBookException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Unit tests for the {@link BookResource} class. */
public class BookResourceTest {

  /** The instance of {@link BookResource} used in the tests. */
  private BookResource bookResource;

  /** The instance of {@link BookService} used in the tests. */
  private BookService bookService;

  /** Runs before each test. */
  @BeforeEach
  void setUp() {
    bookService = mock(BookService.class);
    bookResource = new BookResource(bookService);
  }

  /**
   * Tests that the {@link BookResource#getBookById(String)} method works correctly if the book
   * exists.
   */
  @Test
  void getStockById_whenBookExists_shouldReturnBook() {

    String bookId = "12345-67890";
    Book expectedBook = new Book(bookId, "Some Book", 10);
    when(bookService.getBookById(bookId)).thenReturn(expectedBook);

    ResponseEntity<Book> result = bookResource.getBookById(bookId);
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isEqualTo(expectedBook);

    verify(bookService, times(1)).getBookById(bookId);
  }

  /**
   * Tests that the {@link BookResource#getBookById(String)} throws an exception if the book does
   * not exist.
   */
  @Test
  void getBookById_whenBookDoesNotExist_shouldPropagateException() {

    String bookId = "12345-67890";
    when(bookService.getBookById(bookId)).thenThrow(new NonExistentBookException(bookId));

    // We are unit testing the controller in isolation.
    // This means that the exception handler is not in the context.
    // Then we don't expect a 404 response, but the exception to be propagated.
    assertThatThrownBy(() -> bookResource.getBookById(bookId));
    verify(bookService, times(1)).getBookById(bookId);
  }
}
