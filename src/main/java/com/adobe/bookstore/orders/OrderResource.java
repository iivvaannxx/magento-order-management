package com.adobe.bookstore.orders;

import com.adobe.bookstore.orders.dto.NewOrderDTO;
import com.adobe.bookstore.orders.dto.OrderDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Defines the REST API for {@link Order} objects. */
@RestController
@RequestMapping("/orders")
public class OrderResource {
    
    /** The logger instance for this class. */
    private static final Logger logger = LoggerFactory.getLogger(OrderResource.class);

    /** The singleton instance of {@link OrderService}. */
    private final OrderService orderService;
    
    /** The singleton instance of {@link OrderMapper}. */
    private final OrderMapper orderMapper;
    
    /**
     * Creates a new {@link OrderResource} instance.
     * @param orderService An instance of {@link OrderService}.
     * @param orderMapper An instance of {@link OrderMapper}.
     */
    @Autowired
    public OrderResource(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }
    
    /** Returns a list of {@link OrderDTO} for all the {@link Order} in the repository. */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<OrderDTO>> allOrders() {
        // Even if there are no orders, we return a 200 OK with an empty list (valid JSON).
        // Another option would be to return a 204 No Content (in case there are no orders).
        List<OrderDTO> orders = orderService.getAllOrders().stream()
            .map(orderMapper::toDto)
            .toList();
        
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Creates a new {@link Order} with the given data.
     * @param order The {@link NewOrderDTO} data to create the order.
     */
    @PostMapping
    public ResponseEntity<String> newOrder(@RequestBody NewOrderDTO order) {
        Order newOrder = orderService.createOrder(order);
        logger.info("Created new order: {}", newOrder.getId());
        
        return ResponseEntity.ok(newOrder.getId());
    }
}
