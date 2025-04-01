<script setup>
import { computed } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';

const props = defineProps({
  isCollapsed: {
    type: Boolean,
    default: false
  }
});

const store = useStore();
const route = useRoute();

// Computed properties
const isAdmin = computed(() => store.getters['auth/isAdmin']);
</script>

<template>
  <aside 
    class="h-full bg-white dark:bg-secondary-900 shadow-md transition-all duration-300 overflow-hidden"
    :class="[isCollapsed ? 'w-16' : 'w-64']"
  >
    <div class="h-full px-3 py-4 overflow-y-auto">
      <h2 
        v-if="!isCollapsed" 
        class="text-lg font-semibold mb-4 text-gray-800 dark:text-gray-200 px-3"
      >
        {{ isAdmin ? 'Quản Trị Hệ Thống' : 'Bảng Điều Khiển Thư Viện' }}
      </h2>
      
      <ul class="space-y-2 font-medium">
        <!-- User Navigation -->
        <template v-if="!isAdmin">
          <li>
            <router-link 
              to="/user/profile" 
              class="flex items-center p-3 rounded-lg"
              :class="[
                route.path.startsWith('/user/profile') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800',
                isCollapsed ? 'justify-center' : ''
              ]"
            >
              <span class="material-icons" :class="{ 'mr-3': !isCollapsed }">person</span>
              <span v-if="!isCollapsed">Hồ Sơ Của Tôi</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/user/borrowed" 
              class="flex items-center p-3 rounded-lg"
              :class="[
                route.path.startsWith('/user/borrowed') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800',
                isCollapsed ? 'justify-center' : ''
              ]"
            >
              <span class="material-icons" :class="{ 'mr-3': !isCollapsed }">book</span>
              <span v-if="!isCollapsed">Sách Đã Mượn</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/catalog" 
              class="flex items-center p-3 rounded-lg"
              :class="[
                route.path.startsWith('/catalog') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800',
                isCollapsed ? 'justify-center' : ''
              ]"
            >
              <span class="material-icons" :class="{ 'mr-3': !isCollapsed }">menu_book</span>
              <span v-if="!isCollapsed">Danh Mục Sách</span>
            </router-link>
          </li>
        </template>
        
        <!-- Admin Navigation -->
        <template v-else>
          <li>
            <router-link 
              to="/admin/dashboard" 
              class="flex items-center p-3 rounded-lg"
              :class="[
                route.path === '/admin/dashboard' ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800',
                isCollapsed ? 'justify-center' : ''
              ]"
            >
              <span class="material-icons" :class="{ 'mr-3': !isCollapsed }">dashboard</span>
              <span v-if="!isCollapsed">Bảng Điều Khiển</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/admin/books" 
              class="flex items-center p-3 rounded-lg"
              :class="[
                route.path.startsWith('/admin/books') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800',
                isCollapsed ? 'justify-center' : ''
              ]"
            >
              <span class="material-icons" :class="{ 'mr-3': !isCollapsed }">library_books</span>
              <span v-if="!isCollapsed">Quản Lý Sách</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/admin/users" 
              class="flex items-center p-3 rounded-lg"
              :class="[
                route.path.startsWith('/admin/users') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800',
                isCollapsed ? 'justify-center' : ''
              ]"
            >
              <span class="material-icons" :class="{ 'mr-3': !isCollapsed }">people</span>
              <span v-if="!isCollapsed">Quản Lý Người Dùng</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/admin/borrowings" 
              class="flex items-center p-3 rounded-lg"
              :class="[
                route.path.startsWith('/admin/borrowings') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800',
                isCollapsed ? 'justify-center' : ''
              ]"
            >
              <span class="material-icons" :class="{ 'mr-3': !isCollapsed }">swap_horiz</span>
              <span v-if="!isCollapsed">Quản Lý Mượn Trả</span>
            </router-link>
          </li>
          <li>
            <router-link 
              to="/admin/reports" 
              class="flex items-center p-3 rounded-lg"
              :class="[
                route.path.startsWith('/admin/reports') ? 'bg-primary-100 dark:bg-primary-900 text-primary-700 dark:text-primary-300' : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800',
                isCollapsed ? 'justify-center' : ''
              ]"
            >
              <span class="material-icons" :class="{ 'mr-3': !isCollapsed }">assessment</span>
              <span v-if="!isCollapsed">Báo Cáo</span>
            </router-link>
          </li>
        </template>
      </ul>
    </div>
  </aside>
</template>
