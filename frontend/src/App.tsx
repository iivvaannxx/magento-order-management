import { Books } from "@/components/Books.tsx";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import "./index.css";

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Books />
    </QueryClientProvider>
  );
}

export default App;
