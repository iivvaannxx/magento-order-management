package com.adobe.bookstore.orders.exceptions;

/** Exception thrown when a book is already in an order. */
public class OrderAlreadyContainsBook extends RuntimeException {

  /** The identifier of the book that is already in the order. */
  private final String bookId;

  /**
   * Creates a new instance of the {@link OrderAlreadyContainsBook} class.
   *
   * @param bookId The identifier of the book that is already in the order.
   */
  public OrderAlreadyContainsBook(String bookId) {
    super(String.format("Order already contains book with id \"%s\"", bookId));
    this.bookId = bookId;
  }

  /** Returns the identifier of the book that is already in the order. */
  public String getBookId() {
    return bookId;
  }
}
