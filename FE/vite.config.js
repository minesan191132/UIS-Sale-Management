import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), VueDevTools()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) return;

          if (id.includes('vue') || id.includes('vue-router')) {
            return 'vendor-vue';
          }

          if (id.includes('sweetalert2')) {
            return 'vendor-sweetalert2';
          }

          if (id.includes('chart.js')) {
            return 'vendor-chart';
          }

          if (id.includes('bootstrap') || id.includes('@popperjs/core')) {
            return 'vendor-bootstrap';
          }

          if (id.includes('axios')) {
            return 'vendor-axios';
          }

          return 'vendor-misc';
        },
      },
    },
  },
})
