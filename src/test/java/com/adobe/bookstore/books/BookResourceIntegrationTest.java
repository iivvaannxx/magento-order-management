package com.adobe.bookstore.books;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

/** Integration tests for the {@link BookResource} class. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookResourceIntegrationTest {

  /** The port where the application is running. */
  @LocalServerPort private int port;

  /** The instance of {@link TestRestTemplate}, used for making HTTP requests. */
  @Autowired private TestRestTemplate restTemplate;

  /** The instance of {@link JdbcTemplate}, used for interacting with the database. */
  @Autowired private JdbcTemplate jdbcTemplate;

  /** Runs before each test. */
  @BeforeEach
  public void setUp() {
    insertBook("12345-67890", "Some Book", 7);
  }

  /** Runs after each test. */
  @AfterEach
  public void tearDown() {
    jdbcTemplate.update("DELETE FROM books");
  }

  /**
   * Tests that the {@link BookResource#getBookById(String)} method works correctly if the book
   * exists.
   */
  @Test
  public void getBookById_whenBookExists_shouldReturnBook() {

    String url = getBookUrl("12345-67890");
    Book expectedBook = new Book("12345-67890", "Some Book", 7);

    ResponseEntity<Book> response = restTemplate.getForEntity(url, Book.class);
    Book result = response.getBody();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result).isNotNull();
    assertThat(result.getIsbn()).isEqualTo(expectedBook.getIsbn());
    assertThat(result.getTitle()).isEqualTo(expectedBook.getTitle());
    assertThat(result.getStock()).isEqualTo(expectedBook.getStock());
  }

  /**
   * Tests that the {@link BookResource#getBookById(String)} method returns a 404 Not Found if the
   * book does not exist.
   */
  @Test
  public void getBookById_whenDoesNotExist_shouldReturnNotFound() {

    String url = getBookUrl("22222-22222");
    ResponseEntity<Book> response = restTemplate.getForEntity(url, Book.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  /**
   * Returns the URL of the book stock resource.
   *
   * @param bookId The identifier of the book.
   */
  private String getBookUrl(String bookId) {
    return String.format("http://localhost:%d/api/books/%s", port, bookId);
  }

  /**
   * Inserts a book stock in the database.
   *
   * @param isbn The identifier of the book.
   * @param title The name of the book.
   * @param stock The quantity of the book.
   */
  private void insertBook(String isbn, String title, int stock) {
    jdbcTemplate.update(
        "INSERT INTO books (isbn, title, publish_year, author, stock, price, cover_url) VALUES (?, ?, 2000, 'The Author', ?, 29.99, 'cover')",
        isbn,
        title,
        stock);
  }
}
