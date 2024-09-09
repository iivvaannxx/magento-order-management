import type {Book, Order, OrderItem} from "@/types.ts";

/**
 * Fetches all the books from the backend.
 * @returns A promise that resolves to a list of books.
 */
export async function getBooks(): Promise<Book[]> {
  return fetch("/api/books").then((response) => response.json());
}

/**
 * Fetches the book information for the given book id.
 * @param bookId The id of the book to fetch.
 */
export async function getBookInfo(bookId: string): Promise<Book> {
  return fetch(`/api/books/${bookId}`).then((response) => response.json());
}

/**
 * Fetches all the orders from the backend.
 * @returns A promise that resolves to a list of orders.
 */
export async function getOrders(): Promise<Order[]> {
  return fetch("/api/orders").then((response) => response.json());
}

/**
 * Creates a new order with the given items.
 * @param orderItems The items that form the order.
 */
export async function createOrder(orderItems: OrderItem[]) {
  return fetch("/api/orders", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },

    body: JSON.stringify({ books: orderItems }),
  }).then((response) => {
    return {
      response,
      data: response.json() as unknown as { orderId: string },
    };
  });
}

/**
 * Deletes the order with the given id.
 * @param orderId The id of the order to delete.
 */
export async function deleteOrder(orderId: string) {
  return fetch(`/api/orders/${orderId}`, {
    method: "DELETE",
  });
}
