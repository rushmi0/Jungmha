import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3030
  },
  resolve: {
    alias: {
      process: "process/browser",

      zlib: "browserify-zlib",
      util: 'util',
      crypto: 'crypto-browserify',
      buffer: 'buffer',
    }
  }
})
