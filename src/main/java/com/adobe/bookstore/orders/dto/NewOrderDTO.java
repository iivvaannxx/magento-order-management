package com.adobe.bookstore.orders.dto;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import java.util.List;

/** The DTO for a new order. */
public record NewOrderDTO (
    List<BookOrderDTO> books
) { }
