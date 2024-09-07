package com.adobe.bookstore.bookstock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Defines the repository of {@link BookStock} to manage CRUD operations. */
@Repository
public interface BookStockRepository extends JpaRepository<BookStock, String> {}
