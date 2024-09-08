package com.adobe.bookstore.bookstock;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Defines the REST API for {@link BookStock} objects. */
@RestController
@RequestMapping("/api/books_stock/")
public class BookStockResource {

  /** The singleton instance of {@link BookStockService}. */
  private final BookStockService bookStockService;

  /**
   * Creates a new instance of the {@link BookStockResource} class.
   *
   * @param bookStockService An instance of {@link BookStockService}.
   */
  @Autowired
  public BookStockResource(BookStockService bookStockService) {
    this.bookStockService = bookStockService;
  }

  /**
   * Returns the information of the {@link BookStock} associated with the given identifier.
   *
   * @param bookId The identifier of the {@link BookStock} to retrieve.
   */
  @GetMapping("{bookId}")
  public ResponseEntity<BookStock> getStockById(@PathVariable String bookId) {
    BookStock bookStock = bookStockService.getBookById(bookId);
    return ResponseEntity.ok(bookStock);
  }

  /** Returns all the {@link BookStock} in the repository. */
  @GetMapping
  public ResponseEntity<List<BookStock>> getAllStocks() {
    List<BookStock> bookStocks = bookStockService.getAllBooks();
    return ResponseEntity.ok(bookStocks);
  }
}
