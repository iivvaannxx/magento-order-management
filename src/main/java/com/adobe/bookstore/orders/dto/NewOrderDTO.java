package com.adobe.bookstore.orders.dto;

import com.adobe.bookstore.bookorder.dto.BookOrderDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** The DTO for a new order. */
public record NewOrderDTO (
    
    @Valid
    @NotEmpty(message = "Must contain at least one book.")
    List<BookOrderDTO> books
) { }
