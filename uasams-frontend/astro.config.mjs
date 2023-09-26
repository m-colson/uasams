import { defineConfig } from 'astro/config';
import solidJs from "@astrojs/solid-js";

import node from "@astrojs/node";

// https://astro.build/config
export default defineConfig({
  output: "hybrid",
  integrations: [solidJs()],
  adapter: node({
    mode: "standalone"
  })
});