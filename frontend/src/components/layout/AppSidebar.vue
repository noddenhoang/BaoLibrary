<script setup>
import { computed } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';

const store = useStore();
const route = useRoute();

// Computed properties
const isAdmin = computed(() => store.getters['auth/isAdmin']);
</script>

<template>
  <aside class="w-64 hidden md:block bg-white dark:bg-secondary-900 shadow-md">
    <div class="h-full px-3 py-4 overflow-y-auto">
      <h2 class="text-lg font-semibold mb-4 text-gray-800 dark:text-gray-200 px-3">
        {{ isAdmin ? 'Admin Dashboard' : 'Library Dashboard' }}
      </h2>
      
      <ul class="space-y-2 font-medium">
        <!-- User Navigation -->
        <template v-if="!isAdmin">
          <li>
            <router-link 
              to="/user/profile" 
              class="flex items-center p-3 rounded-lg"
              :class="[route.path.startsWith('/user/profile') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800']"
            >
              <span class="material-icons mr-3">person</span>
              <span>My Profile</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/user/borrowed" 
              class="flex items-center p-3 rounded-lg"
              :class="[route.path.startsWith('/user/borrowed') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800']"
            >
              <span class="material-icons mr-3">book</span>
              <span>Borrowed Books</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/catalog" 
              class="flex items-center p-3 rounded-lg"
              :class="[route.path.startsWith('/catalog') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800']"
            >
              <span class="material-icons mr-3">menu_book</span>
              <span>Book Catalog</span>
            </router-link>
          </li>
        </template>
        
        <!-- Admin Navigation -->
        <template v-else>
          <li>
            <router-link 
              to="/admin/dashboard" 
              class="flex items-center p-3 rounded-lg"
              :class="[route.path === '/admin/dashboard' ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800']"
            >
              <span class="material-icons mr-3">dashboard</span>
              <span>Dashboard</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/admin/books" 
              class="flex items-center p-3 rounded-lg"
              :class="[route.path.startsWith('/admin/books') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800']"
            >
              <span class="material-icons mr-3">library_books</span>
              <span>Manage Books</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/admin/users" 
              class="flex items-center p-3 rounded-lg"
              :class="[route.path.startsWith('/admin/users') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800']"
            >
              <span class="material-icons mr-3">people</span>
              <span>Manage Users</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/admin/borrowings" 
              class="flex items-center p-3 rounded-lg"
              :class="[route.path.startsWith('/admin/borrowings') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800']"
            >
              <span class="material-icons mr-3">swap_horiz</span>
              <span>Borrowings</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/admin/reports" 
              class="flex items-center p-3 rounded-lg"
              :class="[route.path.startsWith('/admin/reports') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800']"
            >
              <span class="material-icons mr-3">assessment</span>
              <span>Reports</span>
            </router-link>
          </li>
        </template>
      </ul>
    </div>
  </aside>
</template> 