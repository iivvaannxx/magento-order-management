package com.adobe.bookstore.bookstock;

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

/** Integration tests for the {@link BookStockResource} class. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookStockResourceIntegrationTest {

  /** The port where the application is running. */
  @LocalServerPort private int port;

  /** The instance of {@link TestRestTemplate}, used for making HTTP requests. */
  @Autowired private TestRestTemplate restTemplate;

  /** The instance of {@link JdbcTemplate}, used for interacting with the database. */
  @Autowired private JdbcTemplate jdbcTemplate;

  /** Runs before each test. */
  @BeforeEach
  public void setUp() {
    insertBookStock("12345-67890", "Some Book", 7);
  }

  /** Runs after each test. */
  @AfterEach
  public void tearDown() {
    jdbcTemplate.update("DELETE FROM book_stock");
  }

  /**
   * Tests that the {@link BookStockResource#getStockById(String)} method works correctly if the
   * book exists.
   */
  @Test
  public void getBookStock_whenBookExists_shouldReturnBookStock() {

    String url = getBookStockUrl("12345-67890");
    BookStock expectedBookStock = new BookStock("12345-67890", "Some Book", 7);

    ResponseEntity<BookStock> response = restTemplate.getForEntity(url, BookStock.class);
    BookStock result = response.getBody();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(expectedBookStock.getId());
    assertThat(result.getName()).isEqualTo(expectedBookStock.getName());
    assertThat(result.getQuantity()).isEqualTo(expectedBookStock.getQuantity());
  }

  /**
   * Tests that the {@link BookStockResource#getStockById(String)} method returns a 404 Not Found if
   * the book does not exist.
   */
  @Test
  public void getBookStock_whenDoesNotExist_shouldReturnNotFound() {

    String url = getBookStockUrl("22222-22222");
    ResponseEntity<BookStock> response = restTemplate.getForEntity(url, BookStock.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  /**
   * Returns the URL of the book stock resource.
   *
   * @param bookId The identifier of the book.
   */
  private String getBookStockUrl(String bookId) {
    return String.format("http://localhost:%d/books_stock/%s", port, bookId);
  }

  /**
   * Inserts a book stock in the database.
   *
   * @param bookId The identifier of the book.
   * @param name The name of the book.
   * @param quantity The quantity of the book.
   */
  private void insertBookStock(String bookId, String name, int quantity) {
    jdbcTemplate.update(
        "INSERT INTO book_stock (id, name, quantity) VALUES (?, ?, ?)", bookId, name, quantity);
  }
}
