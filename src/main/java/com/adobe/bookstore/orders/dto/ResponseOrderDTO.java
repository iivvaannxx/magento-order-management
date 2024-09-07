package com.adobe.bookstore.orders.dto;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import com.adobe.bookstore.orders.Order;

import java.util.List;

/** The DTO used when responding with {@link Order} objects. */
public record ResponseOrderDTO(
    String id,
    List<BookOrderDTO> books
) { }
