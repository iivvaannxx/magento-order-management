package com.adobe.bookstore.orders;

import com.adobe.bookstore.orders.dto.NewOrderDTO;
import com.adobe.bookstore.orders.dto.ResponseOrderDTO;
import com.adobe.bookstore.orders.dto.SuccessfulOrderDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Defines the REST API for {@link Order} objects. */
@RestController
@RequestMapping("/api/orders")
public class OrderResource {

  /** The logger instance for this class. */
  private final Logger logger;

  /** The singleton instance of {@link OrderService}. */
  private final OrderService orderService;

  /** The singleton instance of {@link OrderMapper}. */
  private final OrderMapper orderMapper;

  /**
   * Creates a new {@link OrderResource} instance.
   *
   * @param orderService An instance of {@link OrderService}.
   * @param orderMapper An instance of {@link OrderMapper}.
   * @param logger An instance of {@link Logger}.
   */
  @Autowired
  public OrderResource(OrderService orderService, OrderMapper orderMapper, Logger logger) {
    this.orderService = orderService;
    this.orderMapper = orderMapper;
    this.logger = logger;
  }

  /** Returns a list of {@link ResponseOrderDTO} for all the {@link Order} in the repository. */
  @GetMapping(path = {"", "/"})
  public ResponseEntity<List<ResponseOrderDTO>> allOrders() {
    // Even if there are no orders, we return a 200 OK with an empty list (valid JSON).
    // Another option would be to return a 204 No Content (in case there are no orders).
    List<ResponseOrderDTO> orders =
        orderService.getAllOrders().stream().map(orderMapper::toResponseOrderDto).toList();

    return ResponseEntity.ok(orders);
  }

  /**
   * Returns the {@link ResponseOrderDTO} for the {@link Order} with the given identifier.
   *
   * @param orderId The identifier of the order to retrieve.
   */
  @GetMapping("{orderId}")
  public ResponseEntity<ResponseOrderDTO> getOrderById(@PathVariable String orderId) {
    Order order = orderService.getOrderById(orderId);
    return ResponseEntity.ok(orderMapper.toResponseOrderDto(order));
  }

  /**
   * Creates a new {@link Order} with the given data.
   *
   * @param order The {@link NewOrderDTO} data to create the order.
   */
  @PostMapping
  public ResponseEntity<SuccessfulOrderDTO> newOrder(@Valid @RequestBody NewOrderDTO order) {
    Order newOrder = orderService.createOrder(order);
    logger.info("Created new order: {}", newOrder.getId());

    return ResponseEntity.ok(orderMapper.toSuccessfulOrderDto(newOrder));
  }
}
