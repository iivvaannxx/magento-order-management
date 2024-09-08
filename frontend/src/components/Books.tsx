import { BookItem } from "@/components/BookItem.tsx";
import { getBooks } from "@/lib/api.ts";
import { useQuery } from "@tanstack/react-query";

/** The component for displaying a list of books. */
export function Books() {
  const { data } = useQuery({
    queryKey: ["books"],
    queryFn: getBooks,
  });

  return (
    <div>{data?.map(bookStock => <BookItem stockData={bookStock} />)}</div>
  );
}
