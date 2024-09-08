package com.adobe.bookstore.books;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Defines the REST API for {@link Book} objects. */
@RestController
@RequestMapping("/api/books")
public class BookResource {

  /** The singleton instance of {@link BookService}. */
  private final BookService bookService;

  /**
   * Creates a new instance of the {@link BookResource} class.
   *
   * @param bookService An instance of {@link BookService}.
   */
  @Autowired
  public BookResource(BookService bookService) {
    this.bookService = bookService;
  }

  /**
   * Returns the information of the {@link Book} associated with the given identifier.
   *
   * @param bookIsbn The identifier of the {@link Book} to retrieve.
   */
  @GetMapping("{bookIsbn}")
  public ResponseEntity<Book> getBookById(@PathVariable String bookIsbn) {
    Book book = bookService.getBookById(bookIsbn);
    return ResponseEntity.ok(book);
  }

  /** Returns all the {@link Book} in the repository. */
  @GetMapping
  public ResponseEntity<List<Book>> getAllBooks() {
    List<Book> books = bookService.getAllBooks();
    return ResponseEntity.ok(books);
  }
}
