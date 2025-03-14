import { createVuetify } from 'vuetify';

export default {
  install(app, options) {
    const vuetify = app.config.globalProperties.$vuetify || createVuetify();
    const store = app.config.globalProperties.$store;

    if (!store) {
      console.error('Toast plugin requires Vuex store to be installed first');
      return;
    }
    
    // Define toast functions
    const toast = {
      // Success notification
      success(message, timeout = 3000) {
        vuetify.theme.global.name = 'light';
        store.dispatch('showSnackbar', {
          text: message,
          color: 'success',
          timeout
        });
      },
      
      // Error notification
      error(message, timeout = 5000) {
        vuetify.theme.global.name = 'light';
        store.dispatch('showSnackbar', {
          text: message,
          color: 'error',
          timeout
        });
      },
      
      // Info notification
      info(message, timeout = 3000) {
        vuetify.theme.global.name = 'light';
        store.dispatch('showSnackbar', {
          text: message,
          color: 'info',
          timeout
        });
      },
      
      // Warning notification
      warning(message, timeout = 4000) {
        vuetify.theme.global.name = 'light';
        store.dispatch('showSnackbar', {
          text: message,
          color: 'warning',
          timeout
        });
      }
    };
    
    // Register toast globally
    app.config.globalProperties.$toast = toast;
  }
};
