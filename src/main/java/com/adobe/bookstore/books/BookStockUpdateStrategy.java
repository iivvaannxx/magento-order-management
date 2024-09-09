package com.adobe.bookstore.books;

/** Defines the strategy to use when updating the stock of a book. */
public enum BookStockUpdateStrategy {

  /** Replaces the current stock with the new one. */
  REPLACE,

  /** Adds the new stock to the current one. */
  ADD,

  /** Subtracts the new stock from the current one. */
  SUBTRACT
}
