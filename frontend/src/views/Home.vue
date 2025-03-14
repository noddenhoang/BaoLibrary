<script setup>
import { computed } from 'vue';
import { useStore } from 'vuex';

const store = useStore();
const appName = computed(() => store.getters.appName);
</script>

<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="bg-gradient-to-r from-primary-600 to-primary-800 text-white py-16">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center">
          <h1 class="text-4xl md:text-5xl font-bold mb-4">Welcome to {{ appName }}</h1>
          <p class="text-xl md:text-2xl mb-8 max-w-3xl mx-auto">
            Discover, borrow, and enjoy a vast collection of books at your fingertips.
          </p>
          <div class="flex flex-col sm:flex-row justify-center gap-4">
            <router-link to="/catalog" class="btn bg-white text-primary-700 hover:bg-gray-100">
              Browse Catalog
            </router-link>
            <router-link to="/login" class="btn bg-primary-500 text-white border border-white hover:bg-primary-600">
              Sign In
            </router-link>
          </div>
        </div>
      </div>
    </section>
    
    <!-- Features Section -->
    <section class="py-16 bg-white dark:bg-secondary-950">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 class="text-3xl font-bold text-center mb-12 text-gray-900 dark:text-white">
          Features
        </h2>
        
        <div class="grid md:grid-cols-3 gap-8">
          <!-- Feature 1 -->
          <div class="bg-gray-50 dark:bg-secondary-900 p-6 rounded-lg shadow-md">
            <div class="w-12 h-12 bg-primary-100 dark:bg-primary-900 rounded-full flex items-center justify-center mb-4">
              <span class="material-icons text-primary-600 dark:text-primary-400">search</span>
            </div>
            <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">
              Easy Search
            </h3>
            <p class="text-gray-600 dark:text-gray-300">
              Find books quickly with our powerful search and filtering system.
            </p>
          </div>
          
          <!-- Feature 2 -->
          <div class="bg-gray-50 dark:bg-secondary-900 p-6 rounded-lg shadow-md">
            <div class="w-12 h-12 bg-primary-100 dark:bg-primary-900 rounded-full flex items-center justify-center mb-4">
              <span class="material-icons text-primary-600 dark:text-primary-400">book</span>
            </div>
            <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">
              Digital Borrowing
            </h3>
            <p class="text-gray-600 dark:text-gray-300">
              Borrow books with just a few clicks and manage your reading list online.
            </p>
          </div>
          
          <!-- Feature 3 -->
          <div class="bg-gray-50 dark:bg-secondary-900 p-6 rounded-lg shadow-md">
            <div class="w-12 h-12 bg-primary-100 dark:bg-primary-900 rounded-full flex items-center justify-center mb-4">
              <span class="material-icons text-primary-600 dark:text-primary-400">notifications</span>
            </div>
            <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">
              Reminders
            </h3>
            <p class="text-gray-600 dark:text-gray-300">
              Never miss a return date with our helpful notification system.
            </p>
          </div>
        </div>
      </div>
    </section>
    
    <!-- Call to Action -->
    <section class="py-16 bg-gray-100 dark:bg-secondary-900">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-3xl font-bold mb-4 text-gray-900 dark:text-white">
          Ready to start reading?
        </h2>
        <p class="text-xl mb-8 text-gray-600 dark:text-gray-300 max-w-3xl mx-auto">
          Join our library today and get access to thousands of books.
        </p>
        <router-link to="/register" class="btn btn-primary text-lg px-8 py-3">
          Create an Account
        </router-link>
      </div>
    </section>
  </div>
</template> 