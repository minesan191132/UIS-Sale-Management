import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'
import path from 'path' // Thêm cái này nếu muốn dùng alias @

// https://vite.dev/config/
export default defineConfig({
  // 1. QUAN TRỌNG NHẤT: Thêm dòng base này để Vercel hiểu đường dẫn gốc
  base: '/', 
  
  plugins: [vue(), VueDevTools()],

  // 2. Cấu hình Alias (Tùy chọn nhưng nên có để tránh lỗi ../.. như lúc nãy)
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },

  build: {
    // Đảm bảo output directory là dist (mặc định của Vite)
    outDir: 'dist',
    rollupOptions: {
      output: {
        // Cấu hình chia nhỏ file (giữ nguyên của bạn vì nó tối ưu tốt)
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