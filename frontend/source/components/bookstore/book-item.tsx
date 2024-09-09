import {Button} from "@/components/ui/button.tsx";
import {Input} from "@/components/ui/input.tsx";
import {useEffect, useState} from "react";

import type {Book} from "@/types.ts";
import {Minus, Plus, RefreshCw} from "lucide-react";

/** The props for the {@link BookItem} component. */
export interface BookItemProps {
  data: Book;
  stock: number;
  onAddToOrder?: (book: Book, quantity: number) => void;
  isInOrder?: boolean;
}

/** The component for displaying a book item. */
export function BookItem({
  data,
  stock,
  onAddToOrder,
  isInOrder = false,
}: BookItemProps) {
  const [quantity, setQuantity] = useState(0);

  const handleQuantityChange = (value: string) => {
    const newQuantity = Number.parseInt(value, 10);
    if (
      !Number.isNaN(newQuantity) &&
      newQuantity >= 0 &&
      newQuantity <= data.stock
    ) {
      setQuantity(newQuantity);
    }
  };

  // Reset the quantity when the stock changes
  useEffect(() => {
    setQuantity(0);
  }, [stock]);

  return (
    <article
      key={data.isbn}
      className="flex max-h-[250px] flex-row rounded-lg border p-4 shadow-sm"
    >
      <div className="flex flex-1 flex-col justify-between pr-4">
        <div className="mb-4">
          <h3
            title={data.title}
            className="mb-3 max-w-[15ch] overflow-hidden text-ellipsis whitespace-nowrap text-xl font-semibold"
          >
            {data.title}
          </h3>
          <p
            title={data.author}
            className="mb-1 max-w-[20ch] overflow-hidden text-ellipsis whitespace-nowrap text-gray-600"
          >
            {data.author}
          </p>
          <p className="mb-1 space-x-1 text-sm text-gray-500">
            <span>Year:</span>
            <span>{data.publishYear}</span>
          </p>
          <p className="mb-1 space-x-1 text-sm text-gray-500">
            <span>ISBN:</span>
            <span>{data.isbn}</span>
          </p>
        </div>

        <div>
          <p className="mb-1 ml-1 space-x-2 font-semibold">
            <span>In Stock:</span>
            <span>{data.stock}</span>
          </p>

          <div className="mt-4 flex items-center space-x-3">
            <Input
              type="number"
              min="0"
              max={data.stock}
              value={quantity}
              onChange={(e) => handleQuantityChange(e.target.value)}
              className="mr-2 w-24"
              aria-label={`Quantity for ${data.title}`}
            />
            <Button
              onClick={() => onAddToOrder?.(data, quantity)}
              disabled={quantity === 0 && !isInOrder}
              className="inline-flex items-center justify-center gap-x-2"
            >
              {isInOrder ? (
                quantity === 0 ? (
                  <Minus className="h-4 w-4" />
                ) : (
                  <RefreshCw className="h-4 w-4" />
                )
              ) : (
                <Plus className="h-4 w-4" />
              )}
              <span>
                {isInOrder
                  ? quantity === 0
                    ? "Remove"
                    : "Update"
                  : "Add to Order"}
              </span>
            </Button>
          </div>
        </div>
      </div>

      <div className="mt-4 flex-shrink-0 md:mt-0">
        <div className="relative h-[200px] w-[150px]">
          <img
            src={data.coverUrl}
            alt={`Cover of ${data.title}`}
            width={150}
            className="max-h-full max-w-full rounded-md object-cover object-top"
          />
        </div>
      </div>
    </article>
  );
}
