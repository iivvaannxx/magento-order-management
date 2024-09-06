package com.adobe.bookstore.bookorder.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

/** The DTO for a single book order. */
public record BookOrderDTO (
    @NotEmpty(message = "Book must have an identifier.")
    String bookId,
    
    @Min(value = 1, message = "Quantity must be a positive number greater than 1.")
    Integer quantity
) { }
