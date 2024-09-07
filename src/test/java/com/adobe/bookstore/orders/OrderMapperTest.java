package com.adobe.bookstore.orders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adobe.bookstore.bookorder.BookOrder;
import com.adobe.bookstore.bookorder.BookOrderMapper;
import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import com.adobe.bookstore.bookstock.BookStock;
import com.adobe.bookstore.orders.dto.ResponseOrderDTO;
import com.adobe.bookstore.orders.dto.SuccessfulOrderDTO;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the {@link OrderMapper} class. */
public class OrderMapperTest {

  /** The instance of {@link OrderMapper} used in the tests. */
  private OrderMapper orderMapper;

  /** The mocked instance of {@link BookOrderMapper}. */
  private BookOrderMapper bookOrderMapper;

  /** Runs before each test. */
  @BeforeEach
  public void setUp() {
    bookOrderMapper = mock(BookOrderMapper.class);
    orderMapper = new OrderMapper(bookOrderMapper);
  }

  /** Tests that the {@link OrderMapper#toResponseOrderDto(Order)} method works correctly. */
  @Test
  public void toResponseOrderDto_shouldMapCorrectly() {

    Order order = new Order();
    order.setId("111111-22222");

    // Create a book order with a quantity of 5.
    BookStock book = new BookStock("12345-67890", "Some Book", 10);
    BookOrder bookOrder = new BookOrder(order, book, 5);
    order.setBooks(Set.of(bookOrder));

    // Mock the internal mapper call.
    when(bookOrderMapper.toBookOrderDto(bookOrder))
        .thenReturn(new BookOrderDTO(bookOrder.getBook().getId(), bookOrder.getQuantity()));

    ResponseOrderDTO result = orderMapper.toResponseOrderDto(order);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(order.getId());
    assertThat(result.books()).hasSize(1);
    assertThat(result.books().get(0).bookId()).isEqualTo(bookOrder.getBook().getId());
    assertThat(result.books().get(0).quantity()).isEqualTo(bookOrder.getQuantity());

    verify(bookOrderMapper, times(1)).toBookOrderDto(bookOrder);
  }

  /** Tests that the {@link OrderMapper#toSuccessfulOrderDto(Order)} method works correctly. */
  @Test
  public void toSuccessfulOrderDto_shouldMapCorrectly() {

    Order order = new Order();
    order.setId("111111-22222");

    SuccessfulOrderDTO result = orderMapper.toSuccessfulOrderDto(order);

    assertThat(result).isNotNull();
    assertThat(result.orderId()).isEqualTo(order.getId());
  }
}
