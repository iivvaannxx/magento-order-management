import {getOrders} from "@/lib/api.ts";

import {useQuery} from "@tanstack/react-query";
import {useStore} from "@nanostores/react";

import {queryClient} from "@/store.ts";
import {OrderSummary} from "@/components/bookstore/order-summary.tsx";
import {useEffect} from "react";

/** The component for displaying the list of orders. */
export function OrderList() {
  const client = useStore(queryClient);
  const { data, refetch } = useQuery(
    {
      queryKey: ["orders"],
      queryFn: getOrders,
    },
    client,
  );

  useEffect(() => {
    const eventSource = new EventSource("/api/notifications");
    eventSource.addEventListener("order_update", () => {
      // Invalidate and refetch the orders query
      client.setQueryData(["orders"], null);
      refetch();
    });

    return () => {
      eventSource.close();
    };
  }, [client, refetch]);

  return (
    <div className="mx-auto max-w-3xl">
      <p className="text-xl text-foreground/80">
        There {data?.length === 1 ? "is 1 order" : `are ${data?.length} orders`}{" "}
        in the system:
      </p>
      <ul className="mt-4 flex flex-col gap-4">
        {data?.map((order) => (
          <li
            className="flex w-full items-center justify-between py-2"
            key={order.id}
          >
            <OrderSummary data={order} />
          </li>
        ))}
      </ul>
    </div>
  );
}
