import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '127.0.0.1',
    port: 5173,
    proxy: {
      '/music-service': {
        target: 'https://localhost:5252',
        changeOrigin: true,
        secure: false,
      },
      '/api/v1/grammy': {
        target: 'https://localhost:5314',
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
