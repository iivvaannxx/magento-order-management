import type { BookStock } from "@/types.ts";

/** The props for the {@link BookItem} component. */
export interface BookItemProps {
  stockData: BookStock;
}

/** The component for displaying a book item. */
export function BookItem({ stockData }: BookItemProps) {
  return (
    <article
      key={stockData.id}
      className="border rounded-lg p-4 shadow-sm"
    >
      <h3 className="text-xl font-semibold mb-2">{stockData.name}</h3>
      <p className="mb-4">
        In Stock:
        {stockData.quantity}
      </p>
    </article>
  );
}
