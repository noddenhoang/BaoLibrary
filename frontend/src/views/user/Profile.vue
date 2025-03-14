<template>
  <div class="profile-page py-8">
    <h1 class="text-3xl font-bold mb-6 text-gray-900 dark:text-white">Thông tin cá nhân</h1>
    
    <v-alert
      v-if="error"
      type="error"
      variant="tonal"
      class="mb-4"
      closable
      @click:close="clearError"
    >
      {{ error }}
    </v-alert>
    
    <div v-if="loading" class="flex justify-center my-8">
      <v-progress-circular
        indeterminate
        color="primary"
        size="64"
      ></v-progress-circular>
    </div>
    
    <div v-else-if="user" class="grid grid-cols-1 md:grid-cols-3 gap-8">
      <!-- User info card -->
      <div class="md:col-span-1">
        <div class="bg-white dark:bg-secondary-900 rounded-lg shadow-md p-6">
          <div class="flex flex-col items-center mb-6">
            <div class="w-24 h-24 rounded-full bg-primary-100 dark:bg-primary-900 flex items-center justify-center mb-4">
              <v-icon size="48" color="primary">mdi-account</v-icon>
            </div>
            <h2 class="text-xl font-bold text-gray-900 dark:text-white">{{ user.fullName }}</h2>
            <p class="text-gray-600 dark:text-gray-400">{{ user.username }}</p>
            <v-chip
              class="mt-2"
              :color="roleColor"
              size="small"
              label
            >
              {{ roleLabel }}
            </v-chip>
          </div>
          
          <div class="border-t border-gray-200 dark:border-gray-700 pt-4">
            <div class="flex items-center mb-3">
              <v-icon size="small" color="primary" class="mr-2">mdi-email</v-icon>
              <span class="text-gray-700 dark:text-gray-300">{{ user.email || 'Chưa cập nhật' }}</span>
            </div>
            <div class="flex items-center mb-3">
              <v-icon size="small" color="primary" class="mr-2">mdi-phone</v-icon>
              <span class="text-gray-700 dark:text-gray-300">{{ user.phone || 'Chưa cập nhật' }}</span>
            </div>
            <div class="flex items-start mb-3">
              <v-icon size="small" color="primary" class="mr-2 mt-1">mdi-map-marker</v-icon>
              <span class="text-gray-700 dark:text-gray-300">{{ user.address || 'Chưa cập nhật' }}</span>
            </div>
            <div class="flex items-center">
              <v-icon size="small" color="primary" class="mr-2">mdi-calendar</v-icon>
              <span class="text-gray-700 dark:text-gray-300">Ngày đăng ký: {{ formatDate(user.registrationDate) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- User activity and stats -->
      <div class="md:col-span-2">
        <div class="bg-white dark:bg-secondary-900 rounded-lg shadow-md p-6 mb-6">
          <h3 class="text-xl font-bold mb-4 text-gray-900 dark:text-white">Thống kê hoạt động</h3>
          
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div class="bg-blue-50 dark:bg-blue-900/30 rounded-lg p-4">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Sách đang mượn</p>
                  <p class="text-2xl font-bold text-gray-900 dark:text-white">0</p>
                </div>
                <v-icon color="primary" size="large">mdi-book-open-variant</v-icon>
              </div>
            </div>
            
            <div class="bg-green-50 dark:bg-green-900/30 rounded-lg p-4">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Đã trả</p>
                  <p class="text-2xl font-bold text-gray-900 dark:text-white">0</p>
                </div>
                <v-icon color="success" size="large">mdi-book-check</v-icon>
              </div>
            </div>
            
            <div class="bg-amber-50 dark:bg-amber-900/30 rounded-lg p-4">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Quá hạn</p>
                  <p class="text-2xl font-bold text-gray-900 dark:text-white">0</p>
                </div>
                <v-icon color="warning" size="large">mdi-clock-alert</v-icon>
              </div>
            </div>
          </div>
        </div>
        
        <div class="bg-white dark:bg-secondary-900 rounded-lg shadow-md p-6">
          <h3 class="text-xl font-bold mb-4 text-gray-900 dark:text-white">Sách đang mượn</h3>
          
          <div class="text-center py-8 text-gray-600 dark:text-gray-400">
            <v-icon size="48" color="grey">mdi-book-off</v-icon>
            <p class="mt-2">Bạn chưa mượn sách nào.</p>
            <router-link to="/catalog" class="btn btn-primary mt-4 inline-block">
              Tìm sách ngay
            </router-link>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="text-center py-8">
      <p class="text-gray-600 dark:text-gray-400">Không thể tải thông tin người dùng.</p>
      <router-link to="/" class="btn btn-primary mt-4 inline-block">
        Quay lại trang chủ
      </router-link>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'ProfileView',
  
  data() {
    return {
      localError: null
    };
  },
  
  computed: {
    ...mapGetters('auth', ['user', 'loading', 'error']),
    
    roleColor() {
      const roleColors = {
        admin: 'error',
        manager: 'warning',
        member: 'primary'
      };
      return roleColors[this.user?.role] || 'primary';
    },
    
    roleLabel() {
      const roleLabels = {
        admin: 'Quản trị viên',
        manager: 'Quản lý',
        member: 'Thành viên'
      };
      return roleLabels[this.user?.role] || 'Thành viên';
    }
  },
  
  methods: {
    ...mapActions('auth', ['fetchUserProfile', 'clearError']),
    
    formatDate(dateString) {
      if (!dateString) return 'N/A';
      
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('vi-VN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      }).format(date);
    }
  },
  
  async mounted() {
    this.clearError();
    await this.fetchUserProfile();
  }
};
</script> 