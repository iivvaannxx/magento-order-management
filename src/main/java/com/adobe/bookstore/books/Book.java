package com.adobe.bookstore.books;

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
@Table(name = "books")
@JsonSerialize
public class Book {

  /** The unique identifier of the book. */
  @Id
  @Column(name = "isbn", nullable = false)
  private String isbn;

  /** The name of the book. */
  @Column(name = "title", nullable = false)
  private String title;

  /** The year the book was published */
  @Column(name = "publish_year", nullable = false)
  private Integer publishYear;

  /** The author of the book */
  @Column(name = "author", nullable = false)
  private String author;

  /** The price of the book */
  @Column(name = "price", nullable = false)
  private Double price;

  /** A link to the cover image of the book */
  @Column(name = "cover_url", nullable = false)
  private String coverUrl;

  /** The current stock of the book. */
  @Column(name = "stock", nullable = false)
  private Integer stock;

  /** The set of {@link BookOrder} that have this {@link Book} as an item. */
  @OneToMany(mappedBy = "book")
  private Set<BookOrder> bookOrders;

  /**
   * Creates a new instance of the {@link Book} class.
   *
   * @param isbn The unique identifier of the book.
   * @param title The name of the book.
   * @param stock The current stock of the book.
   */
  public Book(String isbn, String title, Integer stock) {
    this.isbn = isbn;
    this.title = title;
    this.stock = stock;
  }

  public Book() {}

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

  /** Gets the year the book was published */
  public Integer getPublishYear() {
    return publishYear;
  }

  /**
   * Sets the year the book was published
   *
   * @param publishYear The new year.
   */
  public void setPublishYear(Integer publishYear) {
    this.publishYear = publishYear;
  }

  /** Gets the author of the book */
  public String getAuthor() {
    return author;
  }

  /**
   * Sets the author of the book
   *
   * @param author The new author.
   */
  public void setAuthor(String author) {
    this.author = author;
  }

  /** Gets the price of the book */
  public Double getPrice() {
    return price;
  }

  /**
   * Sets the price of the book
   *
   * @param price The new price.
   */
  public void setPrice(Double price) {
    this.price = price;
  }

  /** Gets the cover image url of the book */
  public String getCoverUrl() {
    return coverUrl;
  }

  /**
   * Sets the cover image of the book
   *
   * @param coverUrl The new cover image url.
   */
  public void setCoverUrl(String coverUrl) {
    this.coverUrl = coverUrl;
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
