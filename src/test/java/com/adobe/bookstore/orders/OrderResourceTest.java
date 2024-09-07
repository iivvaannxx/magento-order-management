package com.adobe.bookstore.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adobe.bookstore.orders.dto.NewOrderDTO;
import com.adobe.bookstore.orders.dto.ResponseOrderDTO;
import com.adobe.bookstore.orders.dto.SuccessfulOrderDTO;
import com.adobe.bookstore.orders.exceptions.NonExistentOrderException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Unit tests for the {@link OrderResource} class. */
public class OrderResourceTest {

  /** The instance of {@link OrderResource} used in the tests. */
  private OrderResource orderResource;

  /** The mocked instance of {@link OrderService} used in the tests. */
  private OrderService orderService;

  /** The mocked instance of {@link OrderMapper} used in the tests. */
  private OrderMapper orderMapper;

  /** The mocked instance of {@link Logger} used in the tests. */
  private Logger logger;

  /** Runs before each test. */
  @BeforeEach
  public void setUp() {
    orderService = mock(OrderService.class);
    orderMapper = mock(OrderMapper.class);
    logger = mock(Logger.class);

    orderResource = new OrderResource(orderService, orderMapper, logger);
  }

  /** Tests that the {@link OrderResource#allOrders()} method works correctly. */
  @Test
  void allOrders_shouldReturnListOfOrders() {

    List<Order> orders =
        List.of(
            new Order("11111-22222", Set.of()),
            new Order("33333-44444", Set.of()),
            new Order("55555-66666", Set.of()));

    List<ResponseOrderDTO> responseOrderDTOs =
        List.of(
            new ResponseOrderDTO("11111-22222", List.of()),
            new ResponseOrderDTO("33333-44444", List.of()),
            new ResponseOrderDTO("55555-66666", List.of()));

    when(orderService.getAllOrders()).thenReturn(orders);
    when(orderMapper.toResponseOrderDto(orders.get(0))).thenReturn(responseOrderDTOs.get(0));
    when(orderMapper.toResponseOrderDto(orders.get(1))).thenReturn(responseOrderDTOs.get(1));
    when(orderMapper.toResponseOrderDto(orders.get(2))).thenReturn(responseOrderDTOs.get(2));

    ResponseEntity<List<ResponseOrderDTO>> result = orderResource.allOrders();

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isEqualTo(responseOrderDTOs);

    verify(orderService, times(1)).getAllOrders();
    verify(orderMapper, times(3)).toResponseOrderDto(any(Order.class));
  }

  /** Tests that the {@link OrderResource#getOrderById(String)} method works correctly. */
  @Test
  void getOrderById_whenOrderExists_shouldReturnOrder() {

    String orderId = "11111-22222";
    Order order = new Order(orderId, Set.of());
    ResponseOrderDTO responseOrderDTO = new ResponseOrderDTO(orderId, List.of());

    when(orderService.getOrderById(orderId)).thenReturn(order);
    when(orderMapper.toResponseOrderDto(order)).thenReturn(responseOrderDTO);

    ResponseEntity<ResponseOrderDTO> result = orderResource.getOrderById(orderId);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isEqualTo(responseOrderDTO);

    verify(orderService, times(1)).getOrderById(orderId);
    verify(orderMapper, times(1)).toResponseOrderDto(order);
  }

  /**
   * Tests that the {@link OrderResource#getOrderById(String)} method returns a 404 Not Found if the
   * order does not exist.
   */
  @Test
  void getOrderById_whenOrderDoesNotExist_shouldPropagateException() {

    String orderId = "11111-22222";
    when(orderService.getOrderById(orderId)).thenThrow(new NonExistentOrderException(orderId));

    // We are unit testing the controller in isolation.
    // This means that the exception handler is not in the context.
    // Then we don't expect a 404 response, but the exception to be propagated.
    assertThatThrownBy(() -> orderResource.getOrderById(orderId));
    verify(orderService, times(1)).getOrderById(orderId);
  }

  /** Tests that the {@link OrderResource#newOrder(NewOrderDTO)} method works correctly. */
  @Test
  void newOrder_whenOrderIsValid_shouldReturnSuccessfulOrder() {

    String orderId = "11111-22222";
    Order createdOrder = new Order(orderId, Set.of());

    NewOrderDTO newOrderDTO = new NewOrderDTO(List.of());
    SuccessfulOrderDTO expectedOrderDTO = new SuccessfulOrderDTO(orderId);

    when(orderService.createOrder(newOrderDTO)).thenReturn(createdOrder);
    when(orderMapper.toSuccessfulOrderDto(createdOrder)).thenReturn(expectedOrderDTO);

    ResponseEntity<SuccessfulOrderDTO> result = orderResource.newOrder(newOrderDTO);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isEqualTo(expectedOrderDTO);

    verify(orderService, times(1)).createOrder(newOrderDTO);
    verify(orderMapper, times(1)).toSuccessfulOrderDto(createdOrder);
    verify(logger, times(1)).info("Created new order: {}", orderId);
  }
}
