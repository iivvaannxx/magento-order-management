package com.adobe.bookstore.books;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adobe.bookstore.books.exceptions.NonExistentBookException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the {@link BookService} class. */
class BookServiceTest {

  /** The instance of {@link BookService} used in the tests. */
  private BookService bookService;

  /** The mocked instance of {@link BookRepository} used in the tests. */
  private BookRepository bookRepository;

  /** Runs before each test. */
  @BeforeEach
  void setUp() {
    bookRepository = mock(BookRepository.class);
    bookService = new BookService(bookRepository);
  }

  /** Tests that the {@link BookService#getBookById(String)} method works correctly. */
  @Test
  void getBookById_whenBookExists_shouldReturnBook() {

    String bookId = "12345-67890";
    Book book = new Book(bookId, "Some Book", 10);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    Book result = bookService.getBookById(bookId);

    assertThat(result).isNotNull();
    assertThat(result.getIsbn()).isEqualTo(book.getIsbn());
    assertThat(result.getTitle()).isEqualTo(book.getTitle());
    assertThat(result.getStock()).isEqualTo(book.getStock());

    // Ensure that the repository was only called once.
    verify(bookRepository, times(1)).findById(bookId);
  }

  /**
   * Tests that the {@link BookService#getBookById(String)} method throws an exception if the book
   * does not exist.
   */
  @Test
  void getBookById_whenBookDoesNotExist_shouldThrowException() {

    String bookId = "12345-67890";
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookService.getBookById(bookId))
        .isInstanceOf(NonExistentBookException.class)
        .hasMessage(String.format("Book with id \"%s\" does not exist", bookId));
  }

  /** Tests that the {@link BookService#updateBookStock(String, Integer)} method works correctly. */
  @Test
  void updateBookStock_whenBookExists_shouldUpdateStockCorrectly() {

    String bookId = "12345-67890";
    Book book = new Book(bookId, "Some Book", 10);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(bookRepository.save(book)).thenReturn(book);

    // Check that the stock is updated correctly.
    bookService.updateBookStock(bookId, 5);
    assertThat(book.getStock()).isEqualTo(5);

    // Ensure that the repository was only called once.
    verify(bookRepository, times(1)).findById(bookId);
    verify(bookRepository, times(1)).save(book);
  }

  /**
   * Tests that the {@link BookService#updateBookStock(String, Integer)} method throws an exception
   * if the book does not exist.
   */
  @Test
  void updateBookStock_whenBookDoesNotExist_shouldThrowException() {

    String bookId = "12345-67890";
    Book book = new Book(bookId, "Some Book", 10);

    assertThatThrownBy(() -> bookService.updateBookStock(bookId, 5))
        .isInstanceOf(NonExistentBookException.class)
        .hasMessage(String.format("Book with id \"%s\" does not exist", bookId));
  }

  /**
   * Tests that the {@link BookService#updateBookStock(String, Integer)} method throws an exception
   * if the book does not exist.
   */
  @Test
  void updateBookStock_whenQuantityIsNegative_shouldThrowException() {

    String bookId = "12345-67890";
    Book book = new Book(bookId, "Some Book", 10);

    assertThatThrownBy(() -> bookService.updateBookStock(bookId, -5))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Quantity cannot be negative.");

    // We first check for the quantity, to prevent useless calls to the repository.
    verify(bookRepository, times(0)).findById(bookId);
    verify(bookRepository, times(0)).save(book);
  }
}
