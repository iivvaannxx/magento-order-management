package com.adobe.bookstore.bookstock.exceptions;

import com.adobe.bookstore.bookstock.BookStock;

/** Exception thrown when a {@link BookStock} does not exist. */
public class NonExistantBookException extends RuntimeException {
    
    /** The identifier of the non-existent {@link BookStock}. */
    private final String bookId;
    
    /**
     * Creates a new instance of the {@link NonExistantBookException} class.
     * @param bookId The identifier of the non-existent {@link BookStock}.
     */
    public NonExistantBookException(String bookId) {
        super(String.format("Book with id \"%s\" does not exist.", bookId));
        this.bookId = bookId;
    }
    
    /** Returns the identifier of the non-existent {@link BookStock}. */
    public String getBookId() {
        return bookId;
    }
}
