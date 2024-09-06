package com.adobe.bookstore.orders.dto;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;
import java.util.List;

/** The DTO for a sucessful order. */
public record OrderDTO(
    String id,
    List<BookOrderDTO> books
) { }
