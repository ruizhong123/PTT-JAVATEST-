import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  // 👇 請加上這段 server 代理設定
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 指向你的 Spring Boot 後端 Port
        changeOrigin: true,
      }
    }
  }
})