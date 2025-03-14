import './assets/main.css'
import '@mdi/font/css/materialdesignicons.css'

import { createApp } from 'vue'
import { createStore } from 'vuex'
import { createRouter, createWebHistory } from 'vue-router'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'

import App from './App.vue'
import routes from './router'
import storeConfig from './store'

// Create Vuetify instance
const vuetify = createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          primary: '#0284c7',
          secondary: '#5f6d8e',
          accent: '#e5781a',
          error: '#b91c1c',
          info: '#2196F3',
          success: '#4CAF50',
          warning: '#FB8C00',
          background: '#f8fafc'
        }
      },
      dark: {
        dark: true,
        colors: {
          primary: '#38bdf8',
          secondary: '#94a3bd',
          accent: '#f1b55e',
          error: '#ef4444',
          info: '#64B5F6',
          success: '#81C784',
          warning: '#FFB74D',
          background: '#0f172a'
        }
      }
    }
  }
})

// Create router instance
const router = createRouter({
  history: createWebHistory(),
  routes
})

// Create store instance
const store = createStore(storeConfig)

// Create and mount the app
const app = createApp(App)
app.use(vuetify)
app.use(router)
app.use(store)
app.mount('#app')
