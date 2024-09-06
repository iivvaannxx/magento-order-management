package com.adobe.bookstore.bookstock;

import com.adobe.bookstore.bookstock.exceptions.NonExistentBookException;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service class for managing {@link BookStock} objects. */
@Service
public class BookStockService {
    
    /** The singleton instance of {@link BookStockRepository}. */
    private final BookStockRepository bookStockRepository;
    
    /**
     * Creates a new instance of the {@link BookStockService} class.
     *
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
     * @throws NonExistentBookException If the book does not exist.
     */
    public BookStock getBookById(String bookId) throws NonExistentBookException {
        return bookStockRepository.findById(bookId)
            .orElseThrow(() -> new NonExistentBookException(bookId));
    }
    
    /**
     * Updates the quantity of the {@link BookStock} associated with the given identifier.
     *
     * @param bookId   The identifier of the {@link BookStock} to update.
     * @param quantity The new quantity of the {@link BookStock}.
     * @throws NonExistentBookException If the {@link BookStock} does not exist.
     * @throws IllegalArgumentException If the quantity is negative.
     */
    @Transactional
    public void updateBookStock(String bookId, Integer quantity)
        throws NonExistentBookException, IllegalArgumentException {
        
        BookStock bookStock = bookStockRepository.findById(bookId)
            .orElseThrow(() -> new NonExistentBookException(bookId));
        
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        
        bookStock.setQuantity(quantity);
        bookStockRepository.save(bookStock);
    }
}
