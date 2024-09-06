package com.adobe.bookstore.bookstock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Defines the entity for book stock. */
@Entity
@Table(name = "book_stock")
@JsonSerialize
public class BookStock {

    /** The unique identifier of the book. */
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    /** The name of the book. */
    @Column(name = "name", nullable = false)
    private String name;

    /** The current stock of the book. */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    /** Returns the unique identifier of the book. */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the book.
     * @param id The new identifier.
     */
    public void setId(String id) {
        this.id = id;
    }

    /** Returns the name of the book. */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the book.
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns the stock quantity of the book. */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the stock quantity of the book.
     * @param quantity The new quantity.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
