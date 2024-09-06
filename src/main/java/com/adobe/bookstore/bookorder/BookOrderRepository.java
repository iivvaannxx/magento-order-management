package com.adobe.bookstore.bookorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Defines the repository of {@link BookOrder} to manage CRUD operations. */
@Repository
public interface BookOrderRepository extends JpaRepository<BookOrder, Long> { }
