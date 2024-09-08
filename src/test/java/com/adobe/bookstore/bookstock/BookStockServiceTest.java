package com.adobe.bookstore.bookstock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adobe.bookstore.bookstock.exceptions.NonExistentBookException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the {@link BookStockService} class. */
class BookStockServiceTest {

  /** The instance of {@link BookStockService} used in the tests. */
  private BookStockService bookStockService;

  /** The mocked instance of {@link BookStockRepository} used in the tests. */
  private BookStockRepository bookStockRepository;

  /** Runs before each test. */
  @BeforeEach
  void setUp() {
    bookStockRepository = mock(BookStockRepository.class);
    bookStockService = new BookStockService(bookStockRepository);
  }

  /** Tests that the {@link BookStockService#getBookById(String)} method works correctly. */
  @Test
  void getBookById_whenBookExists_shouldReturnBook() {

    String bookId = "12345-67890";
    BookStock book = new BookStock(bookId, "Some Book", 10);

    when(bookStockRepository.findById(bookId)).thenReturn(Optional.of(book));
    BookStock result = bookStockService.getBookById(bookId);

    assertThat(result).isNotNull();
    assertThat(result.getIsbn()).isEqualTo(book.getIsbn());
    assertThat(result.getTitle()).isEqualTo(book.getTitle());
    assertThat(result.getStock()).isEqualTo(book.getStock());

    // Ensure that the repository was only called once.
    verify(bookStockRepository, times(1)).findById(bookId);
  }

  /**
   * Tests that the {@link BookStockService#getBookById(String)} method throws an exception if the
   * book does not exist.
   */
  @Test
  void getBookById_whenBookDoesNotExist_shouldThrowException() {

    String bookId = "12345-67890";
    when(bookStockRepository.findById(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookStockService.getBookById(bookId))
        .isInstanceOf(NonExistentBookException.class)
        .hasMessage(String.format("Book with id \"%s\" does not exist", bookId));
  }

  /**
   * Tests that the {@link BookStockService#updateBookStock(String, Integer)} method works
   * correctly.
   */
  @Test
  void updateBookStock_whenBookExists_shouldUpdateStockCorrectly() {

    String bookId = "12345-67890";
    BookStock book = new BookStock(bookId, "Some Book", 10);

    when(bookStockRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(bookStockRepository.save(book)).thenReturn(book);

    // Check that the stock is updated correctly.
    bookStockService.updateBookStock(bookId, 5);
    assertThat(book.getStock()).isEqualTo(5);

    // Ensure that the repository was only called once.
    verify(bookStockRepository, times(1)).findById(bookId);
    verify(bookStockRepository, times(1)).save(book);
  }

  /**
   * Tests that the {@link BookStockService#updateBookStock(String, Integer)} method throws an
   * exception if the book does not exist.
   */
  @Test
  void updateBookStock_whenBookDoesNotExist_shouldThrowException() {

    String bookId = "12345-67890";
    BookStock book = new BookStock(bookId, "Some Book", 10);

    assertThatThrownBy(() -> bookStockService.updateBookStock(bookId, 5))
        .isInstanceOf(NonExistentBookException.class)
        .hasMessage(String.format("Book with id \"%s\" does not exist", bookId));
  }

  /**
   * Tests that the {@link BookStockService#updateBookStock(String, Integer)} method throws an
   * exception if the book does not exist.
   */
  @Test
  void updateBookStock_whenQuantityIsNegative_shouldThrowException() {

    String bookId = "12345-67890";
    BookStock book = new BookStock(bookId, "Some Book", 10);

    assertThatThrownBy(() -> bookStockService.updateBookStock(bookId, -5))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Quantity cannot be negative.");

    // We first check for the quantity, to prevent useless calls to the repository.
    verify(bookStockRepository, times(0)).findById(bookId);
    verify(bookStockRepository, times(0)).save(book);
  }
}
