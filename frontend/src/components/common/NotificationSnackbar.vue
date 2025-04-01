<template>
  <v-snackbar
    v-model="showSnackbar"
    :color="notification ? notification.type : 'primary'"
    :timeout="notification ? notification.timeout || 5000 : 5000"
    variant="tonal"
    location="top"
  >
    <div class="d-flex align-center">
      <v-icon
        v-if="notification && notification.type"
        :icon="getIconByType(notification.type)"
        class="mr-2"
      ></v-icon>
      <span>{{ notification ? notification.message : '' }}</span>
    </div>
    
    <template v-slot:actions>
      <v-btn 
        variant="text" 
        icon="mdi-close"
        @click="closeSnackbar"
      ></v-btn>
    </template>
  </v-snackbar>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'NotificationSnackbar',
  
  computed: {
    ...mapState(['notification']),
    
    showSnackbar: {
      get() {
        return !!this.notification;
      },
      set(value) {
        if (!value) {
          this.closeSnackbar();
        }
      }
    }
  },
  
  methods: {
    closeSnackbar() {
      this.$store.commit('SET_NOTIFICATION', null);
    },
    
    getIconByType(type) {
      switch (type) {
        case 'success':
          return 'mdi-check-circle';
        case 'error':
          return 'mdi-alert-circle';
        case 'warning':
          return 'mdi-alert';
        case 'info':
          return 'mdi-information';
        default:
          return 'mdi-bell';
      }
    }
  }
};
</script> 