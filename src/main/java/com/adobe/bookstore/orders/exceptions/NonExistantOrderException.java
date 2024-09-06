package com.adobe.bookstore.orders.exceptions;

import com.adobe.bookstore.orders.Order;

/** Exception thrown when a {@link Order} does not exist. */
public class NonExistantOrderException extends RuntimeException {
    
    /** The identifier of the non-existent {@link Order}. */
    private final String orderId;
    
    /**
     * Creates a new instance of the {@link NonExistantOrderException} class.
     * @param orderId The identifier of the non-existent {@link Order}.
     */
    public NonExistantOrderException(String orderId) {
        super(String.format("Order with id \"%s\" does not exist.", orderId));
        this.orderId = orderId;
    }
    
    /** Returns the identifier of the non-existent {@link Order}. */
    public String getOrderId() {
        return orderId;
    }
}
