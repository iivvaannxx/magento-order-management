import type { BookStock } from "@/types.ts";

/**
 * Fetches all the books from the backend.
 * @returns A promise that resolves to a list of books.
 */
export async function getBooks(): Promise<BookStock[]> {
  return fetch("/api/books_stock/").then(response => response.json());
}
