package com.adobe.bookstore.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Defines the repository of {@link Order} to manage CRUD operations. */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> { }
