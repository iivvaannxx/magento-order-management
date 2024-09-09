package com.adobe.bookstore.books;

import com.adobe.bookstore.books.exceptions.NonExistentBookException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service class for managing {@link Book} objects. */
@Service
public class BookService {

  /** The singleton instance of {@link BookRepository}. */
  private final BookRepository bookRepository;

  /**
   * Creates a new instance of the {@link BookService} class.
   *
   * @param bookRepository An instance of {@link BookRepository}.
   */
  @Autowired
  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  /**
   * Returns the information of the {@link Book} associated with the given identifier.
   *
   * @param bookId The identifier of the book.
   * @throws NonExistentBookException If the book does not exist.
   */
  public Book getBookById(String bookId) throws NonExistentBookException {
    return bookRepository.findById(bookId).orElseThrow(() -> new NonExistentBookException(bookId));
  }

  /**
   * Returns all the {@link Book} in the repository.
   *
   * @return A list of {@link Book} objects.
   */
  public List<Book> getAllBooks() {
    return bookRepository.findAll();
  }

  /**
   * Updates the quantity of the {@link Book} associated with the given identifier.
   *
   * @param bookId The identifier of the {@link Book} to update.
   * @param quantity The new quantity of the {@link Book}.
   * @param strategy The strategy to use when updating the stock.
   * @throws NonExistentBookException If the {@link Book} does not exist.
   * @throws IllegalArgumentException If the quantity is negative.
   */
  @Transactional
  public int updateBookStock(String bookId, Integer quantity, BookStockUpdateStrategy strategy)
      throws NonExistentBookException, IllegalArgumentException {

    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative.");
    }

    BiFunction<Book, Integer, Integer> updateStrategy =
        switch (strategy) {
          case REPLACE -> (book, qty) -> qty;
          case ADD -> (book, qty) -> book.getStock() + qty;
          case SUBTRACT -> (book, qty) -> book.getStock() - qty;
        };

    Book book = this.getBookById(bookId);
    Integer newStock = updateStrategy.apply(book, quantity);

    book.setStock(newStock);
    bookRepository.save(book);
    return newStock;
  }

  /**
   * Updates the quantity of the {@link Book} associated with the given identifier.
   *
   * @param bookId The identifier of the {@link Book} to update.
   * @param quantity The new quantity of the {@link Book}.
   * @throws NonExistentBookException If the {@link Book} does not exist.
   * @throws IllegalArgumentException If the quantity is negative.
   */
  @Transactional
  public int updateBookStock(String bookId, Integer quantity)
      throws NonExistentBookException, IllegalArgumentException {

    return updateBookStock(bookId, quantity, BookStockUpdateStrategy.REPLACE);
  }
}
