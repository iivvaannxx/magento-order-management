package com.adobe.bookstore.orders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import com.adobe.bookstore.bookstock.BookStockService;
import com.adobe.bookstore.orders.dto.NewOrderDTO;
import com.adobe.bookstore.orders.dto.ResponseOrderDTO;
import com.adobe.bookstore.orders.dto.SuccessfulOrderDTO;
import com.adobe.bookstore.orders.exceptions.NonExistentOrderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Slice Web MVC tests for the {@link OrderResource} class. */
@WebMvcTest(OrderResource.class)
public class OrderResourceWebMvcTest {

  /** The instance of {@link MockMvc} used in the tests. */
  @Autowired private MockMvc mvc;

  /** The mocked instance of {@link OrderService} used in the tests. */
  @MockBean private OrderService orderService;

  /** The mocked instance of {@link OrderMapper} used in the tests. */
  @MockBean private OrderMapper orderMapper;

  /** The mocked instance of {@link BookStockService} used in the tests. */
  @MockBean private BookStockService bookStockService;

  /** The mocked instance of {@link Logger} used in the tests. */
  @MockBean private Logger logger;

  /** Tests that the {@link OrderResource#allOrders()} method works correctly. */
  @Test
  void getAllOrders_shouldReturnListOfOrders() throws Exception {

    List<Order> orders =
        List.of(
            new Order("11111-22222", Set.of()),
            new Order("33333-44444", Set.of()),
            new Order("55555-66666", Set.of()));

    when(orderService.getAllOrders()).thenReturn(orders);
    when(orderMapper.toResponseOrderDto(any(Order.class)))
        .thenAnswer(
            invocation -> {
              Order order = (Order) invocation.getArguments()[0];
              return new ResponseOrderDTO(order.getId(), List.of());
            });

    mvc.perform(get("/api/orders/").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].id").value("11111-22222"))
        .andExpect(jsonPath("$[1].id").value("33333-44444"))
        .andExpect(jsonPath("$[2].id").value("55555-66666"));
  }

  /**
   * Tests that the {@link OrderResource#getOrderById(String)} method works correctly if the order
   * exists.
   */
  @Test
  void getOrderById_whenOrderExists_shouldReturnOrder() throws Exception {

    String orderId = "11111-22222";
    Order order = new Order(orderId, Set.of());
    ResponseOrderDTO responseOrderDTO = new ResponseOrderDTO(orderId, List.of());

    when(orderService.getOrderById(orderId)).thenReturn(order);
    when(orderMapper.toResponseOrderDto(order)).thenReturn(responseOrderDTO);

    mvc.perform(get(String.format("/api/orders/%s", orderId)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(orderId));
  }

  /**
   * Tests that the {@link OrderResource#getOrderById(String)} method returns a 404 Not Found if the
   * order does not exist.
   */
  @Test
  void getOrderById_whenOrderDoesNotExist_shouldReturnNotFound() throws Exception {

    String orderId = "11111-22222";
    when(orderService.getOrderById(orderId)).thenThrow(new NonExistentOrderException(orderId));

    // This time we have a Spring context running, so the exception handler is in the context.
    // It will catch the NonExistentOrderException and return a 404 response.
    mvc.perform(get(String.format("/api/orders/%s", orderId)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Order does not exist"))
        .andExpect(jsonPath("$.orderId").value(orderId));
  }

  /**
   * Tests that the {@link OrderResource#newOrder(NewOrderDTO)} method works correctly if the order
   * is valid.
   */
  @Test
  void newOrder_whenOrderIsValid_shouldReturnSuccessfulOrder() throws Exception {

    String orderId = "11111-22222";
    String bookId = "12345-67890";

    Order order = new Order(orderId, Set.of());
    NewOrderDTO newOrderDTO = new NewOrderDTO(List.of(new BookOrderDTO(bookId, 2)));
    SuccessfulOrderDTO expectedOrderDTO = new SuccessfulOrderDTO(orderId);

    when(orderService.createOrder(newOrderDTO)).thenReturn(order);
    when(orderMapper.toSuccessfulOrderDto(order)).thenReturn(expectedOrderDTO);

    mvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newOrderDTO)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.orderId").value(orderId));
  }
}
