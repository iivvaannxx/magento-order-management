package com.adobe.bookstore.bookorder;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import org.springframework.stereotype.Service;

/** Custom mapper for {@link BookOrder} objects. */
@Service
public class BookOrderMapper {

  /**
   * Maps a {@link BookOrder} object to a {@link BookOrderDTO} object.
   *
   * @param bookOrder The {@link BookOrder} object to transform.
   */
  public BookOrderDTO toBookOrderDto(BookOrder bookOrder) {

    if (bookOrder == null) {
      throw new IllegalArgumentException("Book order cannot be null.");
    }

    return new BookOrderDTO(bookOrder.getBook().getIsbn(), bookOrder.getQuantity());
  }
}
