<script setup>
import { computed } from 'vue';
import { useStore } from 'vuex';

const store = useStore();
const appName = computed(() => store.getters.appName);
const currentYear = new Date().getFullYear();
</script>

<template>
  <footer class="bg-white dark:bg-secondary-900 shadow-inner py-6">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex flex-col md:flex-row justify-between items-center">
        <!-- Logo and copyright -->
        <div class="mb-4 md:mb-0">
          <div class="flex items-center">
            <span class="text-primary-600 dark:text-primary-400 text-xl font-bold">{{ appName }}</span>
          </div>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-2">
            &copy; {{ currentYear }} {{ appName }}. All rights reserved.
          </p>
        </div>
        
        <!-- Links -->
        <div class="flex flex-wrap justify-center gap-x-8 gap-y-2">
          <router-link to="/about" class="text-sm text-gray-500 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white">
            About
          </router-link>
          <router-link to="/privacy" class="text-sm text-gray-500 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white">
            Privacy Policy
          </router-link>
          <router-link to="/terms" class="text-sm text-gray-500 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white">
            Terms of Service
          </router-link>
          <router-link to="/contact" class="text-sm text-gray-500 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white">
            Contact
          </router-link>
        </div>
      </div>
    </div>
  </footer>
</template> 