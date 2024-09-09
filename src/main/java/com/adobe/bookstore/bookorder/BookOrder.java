package com.adobe.bookstore.bookorder;

import com.adobe.bookstore.books.Book;
import com.adobe.bookstore.orders.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;

/** Defines the join table entity between {@link Order} and {@link Book}. */
@Entity
@Table(
    name = "book_orders",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"order_id", "book_id"})})
public class BookOrder {

  /** The unique identifier of the ordered book. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The {@link Order}to which the book belongs. */
  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  /** The {@link Book} that was ordered. */
  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;

  /** How many books (with a given identifier) were ordered. */
  private Integer quantity;

  /** Creates an empty instance of the {@link BookOrder} class. */
  public BookOrder() {}

  /**
   * Creates a new instance of the {@link BookOrder} class.
   *
   * @param order The order to which the book belongs.
   * @param book The book that was ordered.
   * @param quantity The quantity of books that were ordered.
   */
  public BookOrder(Order order, Book book, Integer quantity) {
    this.order = order;
    this.book = book;
    this.quantity = quantity;
  }

  /** Returns the full {@link Order} associated with the book order. */
  public Order getOrder() {
    return order;
  }

  /**
   * Sets the {@link Order} to which the book belongs.
   *
   * @param order The new {@link Order} to be associated with the book.
   */
  public void setOrder(Order order) {
    this.order = order;
  }

  /** Returns the {@link Book} that was ordered. */
  public Book getBook() {
    return book;
  }

  /**
   * Sets the {@link Book} that was ordered.
   *
   * @param book The new {@link Book} to be associated with this book order.
   */
  public void setBook(Book book) {
    this.book = book;
  }

  /** Returns the quantity of books that were ordered. */
  public Integer getQuantity() {
    return quantity;
  }

  /**
   * Sets the quantity of books that were ordered.
   *
   * @param quantity The new amount of books.
   */
  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  /** Generates a hash code for the object. */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  /**
   * Compares the object with the given object for equality. Returns true if the objects represent
   * the same entity.
   *
   * @param obj The object to compare with.
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }

    // Check if the same instance and cast to the same type.
    if (!(obj instanceof BookOrder other)) {
      return false;
    }

    return Objects.equals(id, other.id)
        && Objects.equals(order.getId(), other.order.getId())
        && Objects.equals(book.getIsbn(), other.book.getIsbn());
  }
}
