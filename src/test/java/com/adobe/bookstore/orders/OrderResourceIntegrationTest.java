package com.adobe.bookstore.orders;

import static org.assertj.core.api.Assertions.assertThat;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import com.adobe.bookstore.orders.dto.NewOrderDTO;
import com.adobe.bookstore.orders.dto.ResponseOrderDTO;
import com.adobe.bookstore.orders.dto.SuccessfulOrderDTO;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

/** Integration tests for the {@link OrderResource} class. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderResourceIntegrationTest {

  /** The port where the application is running. */
  @LocalServerPort private int port;

  /** The instance of {@link TestRestTemplate}, used for making HTTP requests. */
  @Autowired private TestRestTemplate restTemplate;

  /** The instance of {@link JdbcTemplate}, used for interacting with the database. */
  @Autowired private JdbcTemplate jdbcTemplate;

  /** Runs before each test. */
  @BeforeEach
  void setUp() {
    insertBookStock("12345-67890", "Some Book", 7);
  }

  /** Runs after each test. */
  @AfterEach
  void tearDown() {
    jdbcTemplate.update("DELETE FROM book_orders");
    jdbcTemplate.update("DELETE FROM orders");
    jdbcTemplate.update("DELETE FROM book_stock");
  }

  /**
   * Tests that the {@link OrderResource#newOrder(NewOrderDTO)} method works correctly if the order
   * is valid.
   */
  @Test
  void newOrder_whenOrderIsValid_shouldReturnSuccessfulOrder() {

    String bookId = "12345-67890";
    int orderedQuantity = 2;

    BookOrderDTO bookOrderDTO = new BookOrderDTO(bookId, orderedQuantity);
    NewOrderDTO newOrderDTO = new NewOrderDTO(List.of(bookOrderDTO));

    ResponseEntity<SuccessfulOrderDTO> response =
        restTemplate.postForEntity(getOrdersUrl(), newOrderDTO, SuccessfulOrderDTO.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SuccessfulOrderDTO successfulOrderDTO = response.getBody();
    assertThat(successfulOrderDTO).isNotNull();
    assertThat(successfulOrderDTO.orderId()).isNotEmpty();

    String orderId = successfulOrderDTO.orderId();
    String query = String.format("SELECT COUNT(*) FROM orders WHERE id = '%s'", orderId);
    Integer count = jdbcTemplate.queryForObject(query, Integer.class);

    // The order was successfully created.
    assertThat(count).isEqualTo(1);

    query = String.format("SELECT stock FROM book_stock WHERE isbn = '%s'", bookId);
    Integer remainingStock = jdbcTemplate.queryForObject(query, Integer.class);

    // Initial stock is 7 and we ordered 2 books.
    assertThat(remainingStock).isEqualTo(5);
  }

  /**
   * Tests that the {@link OrderResource#newOrder(NewOrderDTO)} method returns a 400 Bad Request if
   * the order is invalid because it does not contain any books.
   */
  @Test
  void newOrder_whenNoBooksAreOrdered_shouldReturnBadRequest() {

    NewOrderDTO newOrderDTO = new NewOrderDTO(List.of());
    ResponseEntity<Map<String, Object>> response =
        sendPostRequestWhichReturnsGenericMap(newOrderDTO);

    Map<String, Object> errorMap = response.getBody();
    assertContainsValidationError(errorMap, "books", "Must contain at least one book.");
  }

  /**
   * Tests that the {@link OrderResource#newOrder(NewOrderDTO)} method returns a 400 Bad Request if
   * the order already contains a previously processed book.
   */
  @Test
  void newOrder_whenBookAlreadyInOrder_shouldReturnBadRequest() {
    String bookId = "12345-67890";
    int orderedQuantity = 2;

    BookOrderDTO bookOrderDTO = new BookOrderDTO(bookId, orderedQuantity);
    NewOrderDTO newOrderDTO = new NewOrderDTO(List.of(bookOrderDTO, bookOrderDTO));

    ResponseEntity<Map<String, Object>> response =
        sendPostRequestWhichReturnsGenericMap(newOrderDTO);

    Map<String, Object> errorMap = response.getBody();
    assertThat(errorMap).isNotNull();
    assertThat(errorMap).containsEntry("error", "Order already contains book");
    assertThat(errorMap).containsEntry("bookId", bookId);
  }

  /**
   * Tests that the {@link OrderResource#newOrder(NewOrderDTO)} method returns a 409 Conflict if the
   * order is invalid because there are not enough stocks to fulfill the order.
   */
  @Test
  void newOrder_whenNotEnoughStock_shouldReturnConflict() {

    String bookId = "12345-67890";
    int orderedQuantity = 15;

    BookOrderDTO bookOrderDTO = new BookOrderDTO(bookId, orderedQuantity);
    NewOrderDTO newOrderDTO = new NewOrderDTO(List.of(bookOrderDTO));

    ResponseEntity<Map<String, Object>> response =
        sendPostRequestWhichReturnsGenericMap(newOrderDTO);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();

    Map<String, Object> notEnoughStockErrorMap = response.getBody();
    assertThat(notEnoughStockErrorMap).containsEntry("error", "Not enough stock for book");
    assertThat(notEnoughStockErrorMap).containsEntry("bookId", bookId);
    assertThat(notEnoughStockErrorMap).containsEntry("requestedQuantity", orderedQuantity);
    assertThat(notEnoughStockErrorMap).containsEntry("availableQuantity", 7);
  }

  /**
   * Tests that the {@link OrderResource#newOrder(NewOrderDTO)} method returns a 400 Bad Request if
   * the order is invalid because the quantity is negative.
   */
  @Test
  void newOrder_whenInvalidQuantity_shouldReturnBadRequest() {

    String bookId = "12345-67890";
    int orderedQuantity = -1;

    BookOrderDTO bookOrderDTO = new BookOrderDTO(bookId, orderedQuantity);
    NewOrderDTO newOrderDTO = new NewOrderDTO(List.of(bookOrderDTO));

    ResponseEntity<Map<String, Object>> response =
        sendPostRequestWhichReturnsGenericMap(newOrderDTO);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();

    Map<String, Object> errorMap = response.getBody();
    assertContainsValidationError(
        errorMap, "books[0].quantity", "Quantity must be a positive number greater than 1.");
  }

  /** Tests that the {@link OrderResource#allOrders()} method works correctly. */
  @Test
  @Sql(
      statements = {
        "INSERT INTO book_stock (isbn, title, stock) VALUES ('98765-43210', 'Another Book', 10)",
        "INSERT INTO orders (id) VALUES ('11111-22222')",
        "INSERT INTO orders (id) VALUES ('33333-44444')",
        "INSERT INTO orders (id) VALUES ('55555-66666')",
        "INSERT INTO book_orders (id, book_id, order_id, quantity) VALUES (default, '98765-43210', '11111-22222', 2)",
        "INSERT INTO book_orders (id, book_id, order_id, quantity) VALUES (default, '98765-43210', '33333-44444', 1)",
        "INSERT INTO book_orders (id, book_id, order_id, quantity) VALUES (default, '98765-43210', '55555-66666', 3)"
      },
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void getAllOrders_shouldReturnListOfOrders() {

    ParameterizedTypeReference<List<ResponseOrderDTO>> typeRef =
        new ParameterizedTypeReference<>() {};
    RequestEntity<Void> request = RequestEntity.get(getOrdersUrl()).build();
    ResponseEntity<List<ResponseOrderDTO>> response = restTemplate.exchange(request, typeRef);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(3);
    assertThat(response.getBody().get(0).id()).isEqualTo("11111-22222");
    assertThat(response.getBody().get(1).id()).isEqualTo("33333-44444");
    assertThat(response.getBody().get(2).id()).isEqualTo("55555-66666");
  }

  /**
   * Tests that the {@link OrderResource#getOrderById(String)} method works correctly if the order
   * exists.
   */
  @Test
  @Sql(
      statements = {
        "INSERT INTO book_stock (isbn, title, stock) VALUES ('98765-43210', 'Another Book', 10)",
        "INSERT INTO orders (id) VALUES ('11111-22222')",
        "INSERT INTO book_orders (id, book_id, order_id, quantity) VALUES (default, '98765-43210', '11111-22222', 2)"
      },
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void getOrderById_whenOrderExists_shouldReturnOrder() {

    String orderId = "11111-22222";
    ResponseEntity<ResponseOrderDTO> response =
        restTemplate.getForEntity(
            String.format("%s/%s", getOrdersUrl(), orderId), ResponseOrderDTO.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().id()).isEqualTo(orderId);
  }

  /**
   * Tests that the {@link OrderResource#getOrderById(String)} method returns a 404 Not Found if the
   * order does not exist.
   */
  @Test
  void getOrderById_whenOrderDoesNotExist_shouldReturnNotFound() {

    String orderId = "11111-22222";

    ParameterizedTypeReference<Map<String, String>> typeRef = new ParameterizedTypeReference<>() {};
    RequestEntity<Void> request =
        RequestEntity.get(String.format("%s/%s", getOrdersUrl(), orderId)).build();

    ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, typeRef);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().get("error")).isEqualTo("Order does not exist");
    assertThat(response.getBody().get("orderId")).isEqualTo(orderId);
  }

  /** Returns the URL of the {@link OrderResource} resource. */
  private String getOrdersUrl() {
    return String.format("http://localhost:%d/api/orders", port);
  }

  /**
   * Sends a POST request to the {@link OrderResource} and returns the response as a {@link Map}.
   *
   * @param newOrderDTO The {@link NewOrderDTO} to send.
   */
  private ResponseEntity<Map<String, Object>> sendPostRequestWhichReturnsGenericMap(
      NewOrderDTO newOrderDTO) {
    // Need to specify the error map response type
    // Better fix would be to use a custom ErrorDTO class.
    ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<>() {};

    RequestEntity<NewOrderDTO> request = RequestEntity.post(getOrdersUrl()).body(newOrderDTO);
    return restTemplate.exchange(request, typeRef);
  }

  /**
   * Asserts that the given {@link Map} contains a validation error for the given field name and
   * error message.
   *
   * @param errorMap The {@link Map} to check.
   * @param fieldName The name of the field that caused the error.
   * @param errorMessage The error message that was expected.
   */
  private void assertContainsValidationError(
      Map<String, Object> errorMap, String fieldName, String errorMessage) {
    assertThat(errorMap).containsEntry("error", "The request is invalid or malformed");

    Map<String, Object> validationErrors = (Map<String, Object>) errorMap.get("validationErrors");
    assertThat(validationErrors).containsEntry(fieldName, errorMessage);
  }

  /**
   * Inserts a book stock in the database.
   *
   * @param isbn The identifier of the book.
   * @param title The name of the book.
   * @param stock The quantity of the book.
   */
  private void insertBookStock(String isbn, String title, int stock) {
    jdbcTemplate.update(
        "INSERT INTO book_stock (isbn, title, stock) VALUES (?, ?, ?)", isbn, title, stock);
  }
}
