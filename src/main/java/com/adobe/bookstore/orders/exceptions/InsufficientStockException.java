package com.adobe.bookstore.orders.exceptions;

import com.adobe.bookstore.bookstock.BookStock;
import com.adobe.bookstore.orders.Order;

/** Exception thrown when there is not enough stock to fulfill a {@link Order}. */
public class InsufficientStockException extends RuntimeException {

  /** The identifier of the {@link BookStock} that is not available. */
  private final String bookId;

  /** The amount of books that were requested. */
  private final int requestedQuantity;

  /** The amount of books that are available. */
  private final int availableQuantity;

  /**
   * Creates a new instance of the {@link InsufficientStockException} class.
   *
   * @param bookId The identifier of the {@link BookStock} that is not available.
   * @param requestedQuantity The amount of books that were requested.
   * @param availableQuantity The amount of books that are available.
   */
  public InsufficientStockException(String bookId, int requestedQuantity, int availableQuantity) {
    super(
        String.format(
            "Not enough stock for book \"%s\". Requested: %d, Available: %d",
            bookId, requestedQuantity, availableQuantity));

    this.bookId = bookId;
    this.requestedQuantity = requestedQuantity;
    this.availableQuantity = availableQuantity;
  }

  /** Returns the identifier of the {@link BookStock} that is not available. */
  public String getBookId() {
    return bookId;
  }

  /** Returns the amount of books that were requested. */
  public int getRequestedQuantity() {
    return requestedQuantity;
  }

  /** Returns the amount of books that are available. */
  public int getAvailableQuantity() {
    return availableQuantity;
  }
}
