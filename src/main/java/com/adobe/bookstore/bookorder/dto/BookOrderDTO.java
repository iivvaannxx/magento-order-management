package com.adobe.bookstore.bookorder.dto;

/** The DTO for a single book order. */
public record BookOrderDTO (
    String bookId,
    Integer quantity
) { }
