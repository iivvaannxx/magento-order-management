package com.adobe.bookstore.orders;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import com.adobe.bookstore.bookstock.BookStock;
import com.adobe.bookstore.bookstock.BookStockService;
import com.adobe.bookstore.orders.dto.NewOrderDTO;
import com.adobe.bookstore.orders.exceptions.InsufficientStockException;
import com.adobe.bookstore.orders.exceptions.NonExistantOrderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Service class for managing {@link Order} objects. */
@Service
public class OrderService {
    
    /** The logger instance for this class. */
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    /** The singleton instance of {@link OrderRepository}. */
    private final OrderRepository orderRepository;
    
    /** The singleton instance of {@link BookStockService}. */
    private final BookStockService bookStockService;
    
    
    /**
     * Creates a new instance of the {@link OrderService} class.
     * @param orderRepository An instance of {@link OrderRepository}.
     * @param bookStockService An instance of {@link BookStockService}.
     */
    @Autowired
    public OrderService(OrderRepository orderRepository, BookStockService bookStockService) {
        this.orderRepository = orderRepository;
        this.bookStockService = bookStockService;
    }
    
    /** Returns all the {@link Order} in the repository. */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    /**
     * Returns the {@link Order} with the given identifier.
     * @param orderId The identifier of the {@link Order} to retrieve.
     */
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new NonExistantOrderException(orderId));
    }
    
    /**
     * Creates a new {@link Order} and returns it if the operation is successful.
     *
     * @param orderDto The {@link NewOrderDTO} data to create the order.
     * @throws InsufficientStockException If there are not enough stocks to fulfill the order.
     */
    @Transactional
    public Order createOrder(NewOrderDTO orderDto) throws InsufficientStockException {
        
        Order order = new Order();
        Map<String, Integer> newBookStocks = new HashMap<>();
        
        // Check if there are enough stocks to fulfill the order.
        for (BookOrderDTO bookOrderDto : orderDto.books()) {
            
            BookStock book = bookStockService.getBookById(bookOrderDto.bookId());
            Integer stock = book.getQuantity();
            Integer orderQuantity = bookOrderDto.quantity();
            
            // We can't fulfill the order if there are not enough stocks.
            if (stock < orderQuantity) {
                throw new InsufficientStockException(book.getId(), orderQuantity, stock);
            }
            
            order.addBook(book, orderQuantity);
            newBookStocks.put(book.getId(), stock - orderQuantity);
        }
        
        // Save the order immediately.
        Order savedOrder = orderRepository.save(order);
        String orderId = savedOrder.getId();
        
        // And update the stocks asynchronously.
        CompletableFuture.runAsync(() -> {
           try {
               // Update the stocks of the books in the order.
               logger.info("Starting async stock update for order: {}", orderId);
               newBookStocks.forEach(bookStockService::updateBookStock);
               logger.info("Finished async stock update for order: {}", orderId);
               
           } catch (Exception e) {
               // Here we would need to handle this manually.
               // Or we could use some retry mechanism, or compensating transactions.
               logger.error("Error updating book stocks for order {}", orderId, e);
           }
        });
        
        return savedOrder;
    }
}
