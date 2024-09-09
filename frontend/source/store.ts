import {QueryClient} from "@tanstack/react-query";
import {atom} from "nanostores";
import {persistentAtom} from "@nanostores/persistent";

import type {BookItem} from "@/types.ts";

/** The instance of {@link QueryClient} used for fetching. */
export const queryClient = atom(new QueryClient());

/** The current order instance. */
export const currentOrder = persistentAtom<BookItem[]>("currentOrder", [], {
  encode: JSON.stringify,
  decode: JSON.parse,
});
