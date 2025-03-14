<script setup>
import { onMounted, ref } from 'vue';
import { useStore } from 'vuex';

// Define props using Vue 3 syntax
const props = defineProps({
  notification: {
    type: Object,
    required: true
  }
});

const store = useStore();
const isVisible = ref(true);
const progress = ref(100);
const intervalId = ref(null);

// Close the notification
const closeNotification = () => {
  isVisible.value = false;
  clearInterval(intervalId.value);
  store.dispatch('setNotification', null);
};

// Start the auto-close timer
onMounted(() => {
  // Auto-close after 5 seconds with progress bar
  const duration = 5000; // 5 seconds
  const updateInterval = 50; // Update progress every 50ms
  const step = (updateInterval / duration) * 100;
  
  intervalId.value = setInterval(() => {
    progress.value -= step;
    if (progress.value <= 0) {
      closeNotification();
    }
  }, updateInterval);
});

// Get the appropriate color class based on notification type
const getColorClass = () => {
  switch (props.notification.type) {
    case 'success':
      return 'bg-green-100 border-green-500 text-green-800';
    case 'error':
      return 'bg-red-100 border-red-500 text-red-800';
    case 'warning':
      return 'bg-yellow-100 border-yellow-500 text-yellow-800';
    case 'info':
    default:
      return 'bg-blue-100 border-blue-500 text-blue-800';
  }
};

// Get the appropriate icon based on notification type
const getIcon = () => {
  switch (props.notification.type) {
    case 'success':
      return 'check_circle';
    case 'error':
      return 'error';
    case 'warning':
      return 'warning';
    case 'info':
    default:
      return 'info';
  }
};
</script>

<template>
  <transition name="notification">
    <div 
      v-if="isVisible" 
      class="fixed top-4 right-4 z-50 max-w-md shadow-lg rounded-lg border-l-4 overflow-hidden"
      :class="getColorClass()"
    >
      <div class="p-4 flex items-start">
        <div class="flex-shrink-0 mr-3">
          <span class="material-icons">{{ getIcon() }}</span>
        </div>
        <div class="flex-1">
          <h3 v-if="notification.title" class="text-sm font-medium mb-1">
            {{ notification.title }}
          </h3>
          <p class="text-sm">
            {{ notification.message }}
          </p>
        </div>
        <button 
          @click="closeNotification" 
          class="ml-4 flex-shrink-0 text-sm focus:outline-none"
          aria-label="Close notification"
        >
          <span class="material-icons">close</span>
        </button>
      </div>
      
      <!-- Progress bar -->
      <div 
        class="h-1 transition-all duration-50 ease-linear"
        :class="{
          'bg-green-500': notification.type === 'success',
          'bg-red-500': notification.type === 'error',
          'bg-yellow-500': notification.type === 'warning',
          'bg-blue-500': notification.type === 'info' || !notification.type
        }"
        :style="{ width: `${progress}%` }"
      ></div>
    </div>
  </transition>
</template>

<style scoped>
.notification-enter-active,
.notification-leave-active {
  transition: all 0.3s ease;
}

.notification-enter-from {
  transform: translateX(100%);
  opacity: 0;
}

.notification-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style> 