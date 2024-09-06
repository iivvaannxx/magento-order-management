package com.adobe.bookstore.orders;

import com.adobe.bookstore.bookorder.BookOrder;
import com.adobe.bookstore.bookstock.BookStock;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/** Defines the entity for book orders. */
@Entity
@Table(name = "orders")
public class Order {

    /** The unique identifier of the order. */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** The set of individual {@link BookOrder} that are part of this order. */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookOrder> books = new HashSet<>();
    
    /** Creates an empty instance of the {@link Order} class. */
    public Order() {}
    
    /** Returns the unique identifier of the order. */
    public String getId() {
        return id;
    }
    
    /** Returns the set of {@link BookOrder} that are part of the order. */
    public Set<BookOrder> getBooks() {
        return books;
    }
    
    /**
     * Adds a {@link BookStock} to the order.
     *
     * @param book The {@link BookStock} to be added.
     * @param quantity The quantity of books to be added.
     */
    public void addBook(BookStock book, Integer quantity) {
        
        // Check if the book is already in the order.
        Optional<BookOrder> existingBook = books.stream()
            .filter(b -> b.getBook().equals(book))
            .findFirst();
        
        // If the book is already in the order, update the quantity.
        // Otherwise, add the book to the order.
        existingBook.ifPresentOrElse(
            bookOrder -> bookOrder.setQuantity(bookOrder.getQuantity() + quantity),
            () -> books.add(new BookOrder(this, book, quantity))
        );
    }
}
