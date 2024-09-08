package com.adobe.bookstore.books.exceptions;

import com.adobe.bookstore.books.Book;

/** Exception thrown when a {@link Book} does not exist. */
public class NonExistentBookException extends RuntimeException {

  /** The identifier of the non-existent {@link Book}. */
  private final String bookId;

  /**
   * Creates a new instance of the {@link NonExistentBookException} class.
   *
   * @param bookId The identifier of the non-existent {@link Book}.
   */
  public NonExistentBookException(String bookId) {
    super(String.format("Book with id \"%s\" does not exist", bookId));
    this.bookId = bookId;
  }

  /** Returns the identifier of the non-existent {@link Book}. */
  public String getBookId() {
    return bookId;
  }
}
