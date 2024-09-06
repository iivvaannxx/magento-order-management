package com.adobe.bookstore.bookstock;

import com.adobe.bookstore.bookstock.exceptions.NonExistantBookException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service class for managing {@link BookStock} objects. */
@Service
public class BookStockService {
    
    /** The singleton instance of {@link BookStockRepository}. */
    private final BookStockRepository bookStockRepository;
    
    /**
     * Creates a new instance of the {@link BookStockService} class.
     * @param bookStockRepository An instance of {@link BookStockRepository}.
     */
    @Autowired
    public BookStockService(BookStockRepository bookStockRepository) {
        this.bookStockRepository = bookStockRepository;
    }
    
    /**
     * Returns the information of the {@link BookStock} associated with the given identifier.
     *
     * @param bookId The identifier of the book.
     * @throws NonExistantBookException If the book does not exist.
     */
    public BookStock getBookById(String bookId) throws NonExistantBookException {
        return bookStockRepository.findById(bookId)
            .orElseThrow(() -> new NonExistantBookException(bookId));
    }
}
