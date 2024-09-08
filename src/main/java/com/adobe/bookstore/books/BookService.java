package com.adobe.bookstore.books;

import com.adobe.bookstore.books.exceptions.NonExistentBookException;
import jakarta.transaction.Transactional;
import java.util.List;
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
   * @throws NonExistentBookException If the {@link Book} does not exist.
   * @throws IllegalArgumentException If the quantity is negative.
   */
  @Transactional
  public void updateBookStock(String bookId, Integer quantity)
      throws NonExistentBookException, IllegalArgumentException {

    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative.");
    }

    Book book = this.getBookById(bookId);
    book.setStock(quantity);
    bookRepository.save(book);
  }
}
