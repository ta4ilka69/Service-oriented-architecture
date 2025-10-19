import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig(({ mode }) => ({
  base: mode === 'production' ? '/ui/' : '/',
  plugins: [react()],
  server: {
    host: '127.0.0.1',
    port: 5173,
    cors: { origin: '*' },
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    proxy: {
      '/music-bands': {
        target: 'https://localhost:5252',
        changeOrigin: true,
        secure: false,
      },
      '/api/v1/grammy': {
        target: 'https://localhost:5317',
        changeOrigin: true,
        secure: false,
      },
    },
  },
}))
