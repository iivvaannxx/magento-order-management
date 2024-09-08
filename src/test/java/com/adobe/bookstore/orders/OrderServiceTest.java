package com.adobe.bookstore.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import com.adobe.bookstore.bookstock.BookStock;
import com.adobe.bookstore.bookstock.BookStockService;
import com.adobe.bookstore.orders.dto.NewOrderDTO;
import com.adobe.bookstore.orders.exceptions.InsufficientStockException;
import com.adobe.bookstore.orders.exceptions.NonExistentOrderException;
import com.adobe.bookstore.orders.exceptions.OrderAlreadyContainsBook;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

/** Unit tests for the {@link OrderService} class. */
public class OrderServiceTest {

  /** The instance of {@link OrderService} used in the tests. */
  private OrderService orderService;

  /** The mocked instance of {@link BookStockService} used in the tests. */
  private BookStockService bookStockService;

  /** The mocked instance of {@link OrderRepository} used in the tests. */
  private OrderRepository orderRepository;

  /** The mocked instance of {@link Logger} used in the tests. */
  private Logger logger;

  /** Runs before each test. */
  @BeforeEach
  public void setUp() {
    orderRepository = mock(OrderRepository.class);
    bookStockService = mock(BookStockService.class);
    logger = mock(Logger.class);

    orderService = new OrderService(orderRepository, bookStockService, logger);
  }

  /** Tests that the {@link OrderService#getAllOrders()} method works correctly. */
  @Test
  void getAllOrders_shouldReturnAllOrders() {

    List<Order> orders =
        List.of(
            new Order("111111-22222", null),
            new Order("333333-44444", null),
            new Order("555555-66666", null));

    when(orderRepository.findAll()).thenReturn(orders);
    List<Order> result = orderService.getAllOrders();

    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getId()).isEqualTo(orders.get(0).getId());
    assertThat(result.get(1).getId()).isEqualTo(orders.get(1).getId());
    assertThat(result.get(2).getId()).isEqualTo(orders.get(2).getId());

    verify(orderRepository, times(1)).findAll();
  }

  /** Tests that the {@link OrderService#getOrderById(String)} method works correctly. */
  @Test
  void getOrderById_whenOrderExists_shouldReturnOrder() {

    String orderId = "111111-22222";
    Order order = new Order(orderId, null);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    Order result = orderService.getOrderById(orderId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(order.getId());
    assertThat(result.getBooks()).isEqualTo(order.getBooks());

    verify(orderRepository, times(1)).findById(orderId);
  }

  /**
   * Tests that the {@link OrderService#getOrderById(String)} method throws an exception if the
   * order does not exist.
   */
  @Test
  void getOrderById_whenOrderDoesNotExist_shouldThrowException() {

    String orderId = "111111-22222";
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderService.getOrderById(orderId))
        .isInstanceOf(NonExistentOrderException.class)
        .hasMessage(String.format("Order with id \"%s\" does not exist", orderId));
  }

  /**
   * Tests that the {@link OrderService#createOrder(NewOrderDTO)} method works correctly if the
   * order is created.
   */
  @Test
  void createOrder_withEnoughStock_shouldCreateOrderAndUpdateStockAsync() {

    String bookId = "12345-67890";
    String orderId = "111111-22222";
    int orderQuantity = 2;
    int availableStock = 5;

    // Mock all the data.
    BookStock book = new BookStock(bookId, "Some Book", availableStock);
    BookOrderDTO bookOrderDto = new BookOrderDTO(bookId, orderQuantity);
    NewOrderDTO newOrderDto = new NewOrderDTO(List.of(bookOrderDto));

    // Mock the repository interactions.
    when(bookStockService.getBookById(bookId)).thenReturn(book);
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(
            invocation -> {
              Order order = invocation.getArgument(0);
              order.setId(orderId);

              return order;
            });

    Order result = orderService.createOrder(newOrderDto);

    // Verify that the data is correct.
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(orderId);
    assertThat(result.getBooks()).hasSize(1);
    assertThat(result.getBooks().iterator().next().getBook().getIsbn()).isEqualTo(bookId);

    // Verify number of invocations and concurrency when updating the stock.
    verify(orderRepository, times(1)).save(any(Order.class));
    verify(bookStockService, timeout(1000)).updateBookStock(bookId, availableStock - orderQuantity);

    // Verify that the logger was called,
    verify(logger).info("Starting async stock update for order: {}", orderId);
    verify(logger).info("Finished async stock update for order: {}", orderId);
  }

  /**
   * Tests that the {@link OrderService#createOrder(NewOrderDTO)} method throws an exception if the
   * order is not created.
   */
  @Test
  void createOrder_withNotEnoughStock_shouldThrowException() {

    String bookId = "12345-67890";
    int availableStock = 5;
    int requestedQuantity = 10;

    BookStock book = new BookStock(bookId, "Some Book", availableStock);
    BookOrderDTO bookOrderDto = new BookOrderDTO(bookId, requestedQuantity);
    NewOrderDTO newOrderDto = new NewOrderDTO(List.of(bookOrderDto));
    when(bookStockService.getBookById(bookId)).thenReturn(book);

    assertThatThrownBy(() -> orderService.createOrder(newOrderDto))
        .isInstanceOf(InsufficientStockException.class)
        .hasMessage(
            String.format(
                "Not enough stock for book \"%s\". Requested: %d, Available: %d",
                bookId, requestedQuantity, availableStock));

    verify(orderRepository, times(0)).save(any());
    verify(bookStockService, times(0)).updateBookStock(anyString(), anyInt());
  }

  /**
   * Tests that the {@link OrderService#createOrder(NewOrderDTO)} method throws an exception if the
   * order already contains a previously processed book.
   */
  @Test
  void createOrder_withBookAlreadyInOrder_shouldThrowException() {

    String bookId = "12345-67890";
    int availableStock = 5;
    int requestedQuantity = 2;

    BookStock book = new BookStock(bookId, "Some Book", availableStock);
    BookOrderDTO bookOrderDto = new BookOrderDTO(bookId, requestedQuantity);
    NewOrderDTO newOrderDto = new NewOrderDTO(List.of(bookOrderDto, bookOrderDto));
    when(bookStockService.getBookById(bookId)).thenReturn(book);

    assertThatThrownBy(() -> orderService.createOrder(newOrderDto))
        .isInstanceOf(OrderAlreadyContainsBook.class)
        .hasMessage(String.format("Order already contains book with id \"%s\"", bookId));

    verify(orderRepository, times(0)).save(any());
    verify(bookStockService, times(0)).updateBookStock(anyString(), anyInt());
  }
}
