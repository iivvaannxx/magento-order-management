// @ts-check
import react from "@astrojs/react";
import tailwind from "@astrojs/tailwind";

import { defineConfig } from "astro/config";

// https://astro.build/config
export default defineConfig({
  srcDir: "./source",
  outDir: "../src/main/resources/static",

  build: {
    format: "file",
  },

  vite: {
    server: {
      proxy: {
        "/api": {
          target: "http://localhost:8080",
          changeOrigin: true,
        },
      },
    },
  },

  integrations: [react(), tailwind({ applyBaseStyles: false })],
});
