import {currentOrder} from "@/store.ts";
import {useStore} from "@nanostores/react";
import {Button} from "@/components/ui/button.tsx";
import {createOrder} from "@/lib/api.ts";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet.tsx";
import {Ban, Check, List, Trash2} from "lucide-react";

/** The component for displaying the current order. */
export function CurrentOrder() {
  const bookItems = useStore(currentOrder);

  const placeOrder = () => {
    const orderItems = bookItems.map((item) => ({
      bookId: item.book.isbn,
      quantity: item.quantity,
    }));

    createOrder(orderItems).then(({ response }) => {
      if (response.ok) {
        currentOrder.set([]);
      } else {
        alert("Error placing order");
      }
    });
  };

  return (
    <Sheet>
      <SheetTrigger
        title={"Current Order"}
        className="inline-flex w-fit items-center gap-x-4 rounded-md p-2 transition hover:bg-accent/60"
      >
        <p className="inline-flex items-center gap-x-2 font-semibold">
          <List className="h-6 w-6" />
          <span className="text-lg">Current Order</span>
        </p>
      </SheetTrigger>
      <SheetContent>
        <SheetHeader>
          <SheetTitle>Current Order</SheetTitle>
          <SheetDescription>
            Check the current order and update it if needed.
          </SheetDescription>
        </SheetHeader>

        {bookItems.length === 0 && (
          <p className="mt-8 pl-1 text-sm text-foreground/70">
            No books in the order.
          </p>
        )}

        <ul className="mt-8 flex list-disc flex-col gap-4 pl-6">
          {bookItems.map((item) => (
            <li
              key={item.book.isbn}
              className="list-item space-x-2"
            >
              <span className="font-semibold">{item.book.title}</span>
              <span className="text-foreground/70">x{item.quantity}</span>
            </li>
          ))}
        </ul>

        <hr className={`mb-8 ${bookItems.length === 0 ? "" : "mt-8"}`} />

        <div className="flex w-full justify-end gap-4 pl-8">
          <Button
            variant="destructive"
            className="mb-0.5 inline-flex items-center justify-center gap-x-2 font-semibold"
            onClick={() => currentOrder.set([])}
            disabled={bookItems.length === 0}
          >
            <Trash2 className="mb-0.5 h-5 w-5" />
            <span>Clear</span>
          </Button>

          <Button
            className="inline-flex items-center justify-center gap-x-2 font-semibold"
            disabled={bookItems.length === 0}
            onClick={placeOrder}
          >
            {bookItems.length === 0 ? (
              <Ban className="h-5 w-5" />
            ) : (
              <Check className="h-5 w-5" />
            )}
            <span>
              {bookItems.length === 0 ? "Order is Empty" : "Place Order"}
            </span>
          </Button>
        </div>
      </SheetContent>
    </Sheet>
  );
}
