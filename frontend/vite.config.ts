import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173, // debe coincidir con el origen permitido en CorsConfig del backend
    strictPort: true,
  },
})
