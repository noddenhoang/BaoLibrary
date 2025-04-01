<script setup>
import { computed, onMounted, watch } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import AppNavbar from './components/layout/AppNavbar.vue';
import AppSidebar from './components/layout/AppSidebar.vue';
import AppFooter from './components/layout/AppFooter.vue';
import AppNotification from './components/common/AppNotification.vue';
import NotificationSnackbar from './components/common/NotificationSnackbar.vue';

const store = useStore();
const route = useRoute();

// Computed properties
const darkMode = computed(() => store.getters.isDarkMode);
const notification = computed(() => store.getters.notification);
const isLoading = computed(() => store.getters.isLoading);
const isAuthenticated = computed(() => store.getters['auth/isAuthenticated']);
const isAdmin = computed(() => store.getters['auth/isAdmin']);
const snackbar = computed(() => store.getters.snackbar);
const isSidebarOpen = computed(() => store.getters.isSidebarOpen);

// Check if the current route requires sidebar
const showSidebar = computed(() => {
  // Don't show sidebar on login, register, or 404 pages
  return !route.meta.public && isAuthenticated.value;
});

// Toggle sidebar visibility
const toggleSidebar = () => {
  store.dispatch('toggleSidebar');
};

// Set dark mode based on user preference or system preference
onMounted(() => {
  const savedDarkMode = localStorage.getItem('darkMode');
  if (savedDarkMode !== null) {
    store.commit('SET_DARK_MODE', savedDarkMode === 'true');
  } else {
    // Check system preference
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    store.commit('SET_DARK_MODE', prefersDark);
  }
});

// Watch for dark mode changes and update document class
watch(darkMode, (newValue) => {
  if (newValue) {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }
});

// Close snackbar
const closeSnackbar = () => {
  store.commit('SET_SNACKBAR', { show: false });
};
</script>

<template>
  <v-app>
    <div class="app-container" :class="{ 'dark': darkMode }">
      <div class="min-h-screen flex flex-col bg-gray-50 dark:bg-secondary-950 text-gray-900 dark:text-gray-100">
        <!-- Global loading indicator -->
        <div v-if="isLoading" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-primary-500 border-solid"></div>
        </div>
        
        <!-- Global notification -->
        <AppNotification v-if="notification" :notification="notification" />
        
        <!-- Navigation bar -->
        <AppNavbar />
        
        <!-- Main content area with optional sidebar -->
        <div class="flex-grow flex">
          <!-- Sidebar (only shown when authenticated and not on public pages) -->
          <div v-if="showSidebar" class="relative">
            <AppSidebar :is-collapsed="!isSidebarOpen" />
            
            <!-- Toggle sidebar button -->
            <button
              @click="toggleSidebar"
              class="absolute top-4 -right-3 z-10 bg-white dark:bg-secondary-800 shadow-md rounded-full p-1 text-gray-600 dark:text-gray-300 hover:text-primary-600 dark:hover:text-primary-400 focus:outline-none transition-transform"
              :class="{ 'rotate-180': !isSidebarOpen }"
              aria-label="Toggle sidebar"
            >
              <span class="material-icons">{{ isSidebarOpen ? 'chevron_left' : 'chevron_right' }}</span>
            </button>
          </div>
          
          <!-- Main content -->
          <main 
            class="flex-grow p-4 md:p-6 transition-all duration-200"
            :class="{ 'ml-0': !isSidebarOpen || !showSidebar }"
          >
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
            <NotificationSnackbar />
          </main>
        </div>
        
        <!-- Footer -->
        <AppFooter />
      </div>
    </div>
    
    <!-- Global Snackbar for notifications -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="snackbar.timeout"
      location="top"
    >
      {{ snackbar.text }}
      
      <template v-slot:actions>
        <v-btn
          variant="text"
          icon="mdi-close"
          @click="closeSnackbar"
        ></v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>