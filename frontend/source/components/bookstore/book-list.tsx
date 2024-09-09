import {BookItem} from "@/components/bookstore/book-item.tsx";
import {getBooks} from "@/lib/api.ts";

import {useQuery} from "@tanstack/react-query";
import {useStore} from "@nanostores/react";

import {currentOrder, queryClient} from "@/store.ts";
import {useEffect} from "react";
import type {Book} from "@/types.ts";

/** The component for displaying the list of books. */
export function BookList() {
  const client = useStore(queryClient);
  const orderItems = useStore(currentOrder);

  const { data } = useQuery(
    {
      queryKey: ["books"],
      queryFn: getBooks,
    },
    client,
  );

  useEffect(() => {
    const eventSource = new EventSource("/api/notifications");
    eventSource.addEventListener("stock_update", (event) => {
      // Listen for stock update events and update the stock in the UI
      const { bookId, newStock } = JSON.parse(event.data);
      client.setQueryData(["books"], (oldData: Book[]) => {
        return oldData.map((book) => {
          return book.isbn === bookId ? { ...book, stock: newStock } : book;
        });
      });
    });

    eventSource.onerror = (error) => {
      console.error("Error:", error);
    };

    return () => {
      eventSource.close();
    };
  }, [client]);

  return (
    <ul className="mx-auto grid max-w-[1000px] grid-cols-[repeat(auto-fit,minmax(min(100%,450px),1fr))] gap-4">
      {data?.map((book) => (
        <li key={book.isbn}>
          <BookItem
            isInOrder={orderItems.some((item) => item.book.isbn === book.isbn)}
            stock={book.stock}
            onAddToOrder={(book, quantity) => {
              const index = orderItems.findIndex(
                (item) => item.book.isbn === book.isbn,
              );

              // The book is not in the order
              if (index === -1 && quantity > 0) {
                orderItems.push({ book, quantity });
                currentOrder.set([...orderItems]);
              }

              // The book is already in the order
              else if (orderItems[index].quantity != quantity) {
                if (quantity === 0) {
                  orderItems.splice(index, 1);
                } else {
                  orderItems[index].quantity = quantity;
                }

                currentOrder.set([...orderItems]);
              }
            }}
            data={book}
          />
        </li>
      ))}
    </ul>
  );
}
