import type { Order } from "@/types.ts";
import { Button } from "@/components/ui/button.tsx";
import { deleteOrder, getBookInfo } from "@/lib/api.ts";

import { ChevronDown, ChevronUp, Trash2 } from "lucide-react";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";

import { useState } from "react";
import { useStore } from "@nanostores/react";
import { queryClient } from "@/store.ts";
import { useQueries } from "@tanstack/react-query";

export type OrderItemProps = {
  data: Order;
};

/** Displays the summary of an order. */
export function OrderSummary({ data }: OrderItemProps) {
  const [isOpen, setIsOpen] = useState(false);
  const client = useStore(queryClient);

  // Delete the order when the user clicks the delete button.
  const requestDelete = () => {
    deleteOrder(data.id);
  };

  // Query the book information for each book in the order.
  const bookQueries = useQueries(
    {
      queries: data.books.map((book) => ({
        queryKey: ["book", book.bookId],
        queryFn: () => getBookInfo(book.bookId),
        enabled: isOpen, // Only fetch when the collapsible is open
      })),
    },
    client,
  );

  return (
    <Collapsible
      open={isOpen}
      onOpenChange={setIsOpen}
      className={"relative w-full"}
    >
      <div
        className={`flex w-full items-center justify-between gap-x-4 pr-4 transition hover:bg-accent/20 ${isOpen ? "rounded-t-md border-x border-t" : "rounded-md border"}`}
      >
        <CollapsibleTrigger className="w-full p-4 text-left">
          <span className="inline-flex w-full items-center gap-2 pl-1 font-semibold">
            {isOpen ? (
              <ChevronUp className="h-5 w-5" />
            ) : (
              <ChevronDown className="h-5 w-5" />
            )}
            <span className="inline-flex items-center gap-x-2 pl-2">
              <span>Order</span>
              <span className="text-foreground/60">#{data.id}</span>
            </span>
          </span>
        </CollapsibleTrigger>

        <Button
          variant="destructive"
          size="icon"
          className="inline-flex aspect-square items-center justify-center gap-4 p-2"
          onClick={requestDelete}
        >
          <span className="sr-only">Delete Order</span>
          <Trash2 className="mb-0.5 h-5 w-5" />
        </Button>
      </div>

      <CollapsibleContent className="rounded-b border bg-accent/10 p-4">
        <p className="inline-flex items-center gap-x-2 text-sm font-medium">
          <span>Order contains:</span>
          <span className="text-foreground/70">
            {data.books.length} {data.books.length === 1 ? "book" : "books"}
          </span>
        </p>

        <ul className="mt-4 flex list-disc flex-col gap-y-2 pl-8">
          {data.books.map((book, index) => (
            <li
              key={book.bookId}
              className="list-item w-full"
            >
              <div className="flex w-full items-center justify-between">
                <p>
                  {bookQueries[index].isLoading ? (
                    <span className="text-foreground/70">Loading...</span>
                  ) : (
                    <>
                      <span className="max-w-[40ch] overflow-hidden truncate text-ellipsis text-foreground/90">
                        {bookQueries[index].data?.title}
                      </span>
                      <span className="ml-2 text-foreground/70">
                        x{book.quantity}
                      </span>
                    </>
                  )}
                </p>

                <p className="text-sm text-foreground/60">
                  ISBN: {book.bookId}
                </p>
              </div>
            </li>
          ))}
        </ul>
      </CollapsibleContent>
    </Collapsible>
  );
}
