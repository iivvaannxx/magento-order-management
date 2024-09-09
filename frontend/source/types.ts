/** Defines the model for a book */
export interface Book {
  isbn: string;
  title: string;
  publishYear: number;
  author: string;
  stock: number;
  price: number;
  coverUrl: string;
}

/** Defines the model for an order */
export interface Order {
  id: string;
  books: OrderItem[];
}

/** Defines the model for an order item (server side) */
export interface OrderItem {
  bookId: string;
  quantity: number;
}

/** Defines the model for a book order (client side) */
export interface BookItem {
  book: Book;
  quantity: number;
}
