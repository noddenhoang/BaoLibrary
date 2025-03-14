/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f0f9ff',
          100: '#e0f2fe',
          200: '#bae6fd',
          300: '#7dd3fc',
          400: '#38bdf8',
          500: '#0ea5e9',
          600: '#0284c7',
          700: '#0369a1',
          800: '#075985',
          900: '#0c4a6e',
          950: '#082f49',
        },
        secondary: {
          50: '#f5f7fa',
          100: '#ebeef5',
          200: '#d9dfe8',
          300: '#b9c5d5',
          400: '#94a3bd',
          500: '#7686a6',
          600: '#5f6d8e',
          700: '#4f5a75',
          800: '#444d62',
          900: '#3a4052',
          950: '#25293a',
        },
        accent: {
          50: '#fdf5e9',
          100: '#fbe8c9',
          200: '#f6d095',
          300: '#f1b55e',
          400: '#ed9a33',
          500: '#e5781a',
          600: '#cc5c14',
          700: '#a84013',
          800: '#883317',
          900: '#712c16',
          950: '#3f150a',
        },
      }
    },
  },
  plugins: [],
} 