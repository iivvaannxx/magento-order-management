package com.adobe.bookstore.books;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Defines the repository of {@link Book} to manage CRUD operations. */
@Repository
public interface BookRepository extends JpaRepository<Book, String> {}
