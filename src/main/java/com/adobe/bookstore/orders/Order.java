package com.adobe.bookstore.orders;

import com.adobe.bookstore.bookorder.BookOrder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
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

  /**
   * Sets the unique identifier of the order.
   *
   * @param id The new identifier.
   */
  public void setId(String id) {
    this.id = id;
  }

  /** Returns the set of {@link BookOrder} that are part of the order. */
  public Set<BookOrder> getBooks() {
    return books;
  }

  /**
   * Sets the set of {@link BookOrder} that are part of the order.
   *
   * @param books The new set of {@link BookOrder}.
   */
  public void setBooks(Set<BookOrder> books) {
    this.books = books;
  }
}
