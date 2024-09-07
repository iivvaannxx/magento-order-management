package com.adobe.bookstore.bookorder;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import com.adobe.bookstore.bookstock.BookStock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Unit tests for the {@link BookOrderMapper} class. */
public class BookOrderMapperTest {
    
    /** The singleton instance of {@link BookOrderMapper}. */
    private BookOrderMapper bookOrderMapper;
    
    /** Runs before each test. */
    @BeforeEach
    public void setUp() {
        bookOrderMapper = new BookOrderMapper();
    }
    
    /** Tests that the {@link BookOrderMapper#toBookOrderDto(BookOrder)} method works correctly. */
    @Test
    public void toBookOrderDto_shouldMapCorrectly() {
        
        BookStock book = new BookStock();
        book.setId("12345-67890");
        book.setName("some book");
        book.setQuantity(10);
        
        BookOrder bookOrder = new BookOrder();
        bookOrder.setBook(book);
        bookOrder.setQuantity(5);
        
        BookOrderDTO result = bookOrderMapper.toBookOrderDto(bookOrder);
        
        assertThat(result).isNotNull();
        assertThat(result.bookId()).isEqualTo("12345-67890");
        assertThat(result.quantity()).isEqualTo(5);
    }
    
    /** Tests that the {@link BookOrderMapper#toBookOrderDto(BookOrder)} method throws an exception when the book order is null. */
    @Test
    public void toBookOrderDto_shouldThrowExceptionWhenBookOrderIsNull() {
        
        assertThatThrownBy(() -> bookOrderMapper.toBookOrderDto(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Book order cannot be null.");
    }
}
