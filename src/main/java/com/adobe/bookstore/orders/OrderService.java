package com.adobe.bookstore.orders;

import com.adobe.bookstore.UpdateController;
import com.adobe.bookstore.bookorder.BookOrder;
import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import com.adobe.bookstore.books.Book;
import com.adobe.bookstore.books.BookService;
import com.adobe.bookstore.books.BookStockUpdateStrategy;
import com.adobe.bookstore.orders.dto.NewOrderDTO;
import com.adobe.bookstore.orders.exceptions.InsufficientStockException;
import com.adobe.bookstore.orders.exceptions.NonExistentOrderException;
import com.adobe.bookstore.orders.exceptions.OrderAlreadyContainsBook;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service class for managing {@link Order} objects. */
@Service
public class OrderService {

  /** The logger instance for this class. */
  private final Logger logger;

  /** The singleton instance of {@link OrderRepository}. */
  private final OrderRepository orderRepository;

  /** The singleton instance of {@link BookService}. */
  private final BookService bookService;

  /** The singleton instance of {@link UpdateController}. */
  private final UpdateController updateController;

  /**
   * Creates a new instance of the {@link OrderService} class.
   *
   * @param orderRepository An instance of {@link OrderRepository}.
   * @param bookService An instance of {@link BookService}.
   * @param logger An instance of {@link Logger}.
   * @param updateController An instance of {@link UpdateController}.
   */
  @Autowired
  public OrderService(
      OrderRepository orderRepository,
      BookService bookService,
      Logger logger,
      UpdateController updateController) {
    this.orderRepository = orderRepository;
    this.bookService = bookService;
    this.logger = logger;
    this.updateController = updateController;
  }

  /** Returns all the {@link Order} in the repository. */
  public List<Order> getAllOrders() {
    return orderRepository.findAll();
  }

  /**
   * Returns the {@link Order} with the given identifier.
   *
   * @param orderId The identifier of the {@link Order} to retrieve.
   */
  public Order getOrderById(String orderId) {
    return orderRepository
        .findById(orderId)
        .orElseThrow(() -> new NonExistentOrderException(orderId));
  }

  /**
   * Deletes the {@link Order} with the given identifier.
   *
   * @param orderId The identifier of the {@link Order} to delete.
   */
  public void deleteOrderById(String orderId) {

    // Return the stock to the books.
    Optional<Order> order = orderRepository.findById(orderId);

    if (order.isEmpty()) {
      return;
    }

    Set<BookOrder> bookOrders = order.get().getBooks();
    Map<String, Integer> orderStocks = new HashMap<>();

    // Collect the stocks of the books in the order.
    bookOrders.forEach(
        bookOrder -> {
          orderStocks.put(bookOrder.getBook().getIsbn(), bookOrder.getQuantity());
        });

    // Update the stocks asynchronously.
    updateBookStocksAsync(orderId, orderStocks, BookStockUpdateStrategy.ADD);

    // And delete immediately.
    orderRepository.deleteById(orderId);
    updateController.sendOrderUpdate(orderId);
  }

  /**
   * Creates a new {@link Order} and returns it if the operation is successful.
   *
   * @param orderDto The {@link NewOrderDTO} data to create the order.
   * @throws InsufficientStockException If there are not enough stocks to fulfill the order.
   */
  @Transactional
  public Order createOrder(NewOrderDTO orderDto) throws InsufficientStockException {

    logger.info("Creating new order: {}", orderDto);

    Order order = new Order();
    Map<String, Integer> newBookStocks = new HashMap<>();
    Set<String> bookIds = new HashSet<>();

    // Check if there are enough stocks to fulfill the order.
    for (BookOrderDTO bookOrderDto : orderDto.books()) {

      Book book = bookService.getBookById(bookOrderDto.bookId());
      Integer stock = book.getStock();
      Integer orderQuantity = bookOrderDto.quantity();

      if (!(bookIds.add(book.getIsbn()))) {
        throw new OrderAlreadyContainsBook(book.getIsbn());
      }

      // We can't fulfill the order if there are not enough stocks.
      if (stock < orderQuantity) {
        throw new InsufficientStockException(book.getIsbn(), orderQuantity, stock);
      }

      Integer newStock = addBookToOrder(order, book, orderQuantity);
      newBookStocks.put(book.getIsbn(), newStock);
    }

    // Save the order immediately.
    Order savedOrder = orderRepository.save(order);
    String orderId = savedOrder.getId();
    updateController.sendOrderUpdate(orderId);

    // And update the stocks asynchronously.
    updateBookStocksAsync(orderId, newBookStocks, BookStockUpdateStrategy.REPLACE);
    return savedOrder;
  }

  /**
   * Adds a {@link Book} to the order and returns the stock amount that needs to be left after the
   * addition.
   *
   * @param order The {@link Order} to which the book belongs.
   * @param book The {@link Book} to be added.
   * @param quantity The quantity of books to be added.
   */
  private Integer addBookToOrder(Order order, Book book, Integer quantity) {

    // Check if the book is already in the order.
    Set<BookOrder> books = order.getBooks();
    boolean added = books.add(new BookOrder(order, book, quantity));

    logger.info("Added book {} to order {}? {}", book.getIsbn(), order.getId(), added);

    return book.getStock() - quantity;
  }

  /**
   * Updates the stocks of the books in a recently placed order asynchronously.
   *
   * @param orderId The identifier of the order.
   * @param quantities The new stocks of the books in the order.
   * @param strategy The strategy to use when updating the stock.
   */
  private void updateBookStocksAsync(
      String orderId, Map<String, Integer> quantities, BookStockUpdateStrategy strategy) {
    CompletableFuture.runAsync(
        () -> {
          try {
            // Update the stocks of the books in the order.
            logger.info("Starting async stock update for order: {}", orderId);
            quantities.forEach(
                (bookId, quantity) -> {
                  int resultingStock = bookService.updateBookStock(bookId, quantity, strategy);
                  updateController.sendStockUpdate(bookId, resultingStock);
                });

            logger.info("Finished async stock update for order: {}", orderId);

          } catch (Exception e) {
            // Here we would need to handle this manually.
            // Or we could use some retry mechanism, or compensating transactions.
            logger.error("Error updating book stocks for order {}", orderId, e);
          }
        });
  }
}
