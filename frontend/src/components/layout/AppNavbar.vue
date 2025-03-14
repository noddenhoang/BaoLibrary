<script setup>
import { computed, ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import authService from '@/services/auth';

const store = useStore();
const router = useRouter();

// Computed properties
const darkMode = computed(() => store.getters.isDarkMode);
const isAuthenticated = computed(() => store.getters['auth/isAuthenticated']);
const user = computed(() => store.getters['auth/user']);
const appName = computed(() => store.getters.appName);

// Mobile menu state
const mobileMenuOpen = ref(false);

// Toggle dark mode
const toggleDarkMode = () => {
  store.dispatch('toggleDarkMode');
};

// Toggle mobile menu
const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value;
};

// Handle logout
const handleLogout = () => {
  authService.logout();
  router.push('/login');
  store.dispatch('setNotification', {
    type: 'success',
    message: 'You have been successfully logged out.'
  });
};
</script>

<template>
  <nav class="bg-white dark:bg-secondary-900 shadow-md">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <!-- Logo and main navigation -->
        <div class="flex">
          <!-- Logo -->
          <div class="flex-shrink-0 flex items-center">
            <router-link to="/" class="flex items-center">
              <span class="text-primary-600 dark:text-primary-400 text-2xl font-bold">
                {{ appName }}
              </span>
            </router-link>
          </div>
          
          <!-- Desktop Navigation Links -->
          <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
            <router-link 
              to="/" 
              class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              :class="[$route.path === '/' ? 'border-primary-500 text-gray-900 dark:text-white' : 'border-transparent text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200']"
            >
              Home
            </router-link>
            
            <router-link 
              to="/catalog" 
              class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              :class="[$route.path.startsWith('/catalog') ? 'border-primary-500 text-gray-900 dark:text-white' : 'border-transparent text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200']"
            >
              Book Catalog
            </router-link>
            
            <template v-if="isAuthenticated">
              <router-link 
                to="/user/borrowed" 
                class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
                :class="[$route.path.startsWith('/user/borrowed') ? 'border-primary-500 text-gray-900 dark:text-white' : 'border-transparent text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200']"
              >
                My Books
              </router-link>
            </template>
          </div>
        </div>
        
        <!-- Right side buttons -->
        <div class="flex items-center">
          <!-- Dark mode toggle -->
          <button 
            @click="toggleDarkMode" 
            class="p-2 rounded-full text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200 focus:outline-none"
            aria-label="Toggle dark mode"
          >
            <span v-if="darkMode" class="material-icons">light_mode</span>
            <span v-else class="material-icons">dark_mode</span>
          </button>
          
          <!-- User menu -->
          <div v-if="isAuthenticated" class="ml-3 relative">
            <div class="flex items-center space-x-3">
              <router-link 
                to="/user/profile" 
                class="text-sm font-medium text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
              >
                {{ user?.username || 'User' }}
              </router-link>
              
              <button 
                @click="handleLogout" 
                class="btn btn-outline text-sm"
              >
                Logout
              </button>
            </div>
          </div>
          
          <!-- Login/Register buttons -->
          <div v-else class="ml-3 flex items-center space-x-2">
            <router-link to="/login" class="btn btn-outline text-sm">
              Login
            </router-link>
            <router-link to="/register" class="btn btn-primary text-sm">
              Register
            </router-link>
          </div>
          
          <!-- Mobile menu button -->
          <div class="flex items-center sm:hidden ml-3">
            <button 
              @click="toggleMobileMenu" 
              class="inline-flex items-center justify-center p-2 rounded-md text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200 focus:outline-none"
              aria-expanded="false"
            >
              <span class="sr-only">Open main menu</span>
              <span v-if="!mobileMenuOpen" class="material-icons">menu</span>
              <span v-else class="material-icons">close</span>
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Mobile menu -->
    <div v-if="mobileMenuOpen" class="sm:hidden">
      <div class="pt-2 pb-3 space-y-1">
        <router-link 
          to="/" 
          class="block pl-3 pr-4 py-2 border-l-4 text-base font-medium"
          :class="[$route.path === '/' ? 'border-primary-500 text-primary-700 dark:text-primary-300 bg-primary-50 dark:bg-primary-900 bg-opacity-50' : 'border-transparent text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200']"
          @click="mobileMenuOpen = false"
        >
          Home
        </router-link>
        
        <router-link 
          to="/catalog" 
          class="block pl-3 pr-4 py-2 border-l-4 text-base font-medium"
          :class="[$route.path.startsWith('/catalog') ? 'border-primary-500 text-primary-700 dark:text-primary-300 bg-primary-50 dark:bg-primary-900 bg-opacity-50' : 'border-transparent text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200']"
          @click="mobileMenuOpen = false"
        >
          Book Catalog
        </router-link>
        
        <template v-if="isAuthenticated">
          <router-link 
            to="/user/borrowed" 
            class="block pl-3 pr-4 py-2 border-l-4 text-base font-medium"
            :class="[$route.path.startsWith('/user/borrowed') ? 'border-primary-500 text-primary-700 dark:text-primary-300 bg-primary-50 dark:bg-primary-900 bg-opacity-50' : 'border-transparent text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200']"
            @click="mobileMenuOpen = false"
          >
            My Books
          </router-link>
          
          <router-link 
            to="/user/profile" 
            class="block pl-3 pr-4 py-2 border-l-4 text-base font-medium"
            :class="[$route.path.startsWith('/user/profile') ? 'border-primary-500 text-primary-700 dark:text-primary-300 bg-primary-50 dark:bg-primary-900 bg-opacity-50' : 'border-transparent text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200']"
            @click="mobileMenuOpen = false"
          >
            Profile
          </router-link>
          
          <button 
            @click="handleLogout" 
            class="block w-full text-left pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200"
          >
            Logout
          </button>
        </template>
        
        <template v-else>
          <router-link 
            to="/login" 
            class="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200"
            @click="mobileMenuOpen = false"
          >
            Login
          </router-link>
          
          <router-link 
            to="/register" 
            class="block pl-3 pr-4 py-2 border-l-4 border-transparent text-base font-medium text-gray-500 dark:text-gray-300 hover:text-gray-700 dark:hover:text-gray-200"
            @click="mobileMenuOpen = false"
          >
            Register
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template> 