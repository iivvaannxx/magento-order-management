package com.adobe.bookstore.orders;

import com.adobe.bookstore.bookorder.BookOrderMapper;
import com.adobe.bookstore.orders.dto.ResponseOrderDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/** Custom mapper for {@link Order} objects. */
@Service
public class OrderMapper {
    
    /** The singleton instance of {@link BookOrderMapper}. */
    private final BookOrderMapper bookOrderMapper;
    
    /** Creates a new instance of the {@link OrderMapper} class. */
    @Autowired
    public OrderMapper(BookOrderMapper bookOrderMapper) {
        this.bookOrderMapper = bookOrderMapper;
    }
    
    /**
     * Maps an {@link Order} object to an {@link ResponseOrderDTO} object.
     * @param order The {@link Order} object to transform.
     */
    public ResponseOrderDTO toResponseOrderDto(Order order) {

        return new ResponseOrderDTO(
            order.getId(),
            order.getBooks().stream()
                .map(bookOrderMapper::toDto)
                .collect(Collectors.toList())
        );
    }
}
