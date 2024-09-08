package com.adobe.bookstore.bookstock;

import com.adobe.bookstore.bookorder.BookOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;

/** Defines the entity for book stock. */
@Entity
@Table(name = "book_stock")
@JsonSerialize
public class BookStock {

  /** The unique identifier of the book. */
  @Id
  @Column(name = "isbn", nullable = false)
  private String isbn;

  /** The name of the book. */
  @Column(name = "title", nullable = false)
  private String title;

  /** The current stock of the book. */
  @Column(name = "stock", nullable = false)
  private Integer stock;

  /** The set of {@link BookOrder} that have this {@link BookStock} as an item. */
  @OneToMany(mappedBy = "book")
  private Set<BookOrder> bookOrders;

  /**
   * Creates a new instance of the {@link BookStock} class.
   *
   * @param isbn The unique identifier of the book.
   * @param title The name of the book.
   * @param stock The current stock of the book.
   */
  public BookStock(String isbn, String title, Integer stock) {
    this.isbn = isbn;
    this.title = title;
    this.stock = stock;
  }

  public BookStock() {}

  /** Returns the unique identifier of the book. */
  public String getIsbn() {
    return isbn;
  }

  /**
   * Sets the unique identifier of the book.
   *
   * @param id The new identifier.
   */
  public void setIsbn(String id) {
    this.isbn = id;
  }

  /** Returns the name of the book. */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the name of the book.
   *
   * @param name The new name.
   */
  public void setTitle(String name) {
    this.title = name;
  }

  /** Returns the stock quantity of the book. */
  public Integer getStock() {
    return stock;
  }

  /**
   * Sets the stock quantity of the book.
   *
   * @param quantity The new quantity.
   */
  public void setStock(Integer quantity) {
    this.stock = quantity;
  }
}
