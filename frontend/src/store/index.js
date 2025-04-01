// Import store modules
import auth from './modules/auth';
import books from './modules/books';
import borrowings from './modules/borrowings';
import categories from './modules/categories';
import authors from './modules/authors';
import branches from './modules/branches';
import inventory from './modules/inventory';
import notifications from './modules/notifications';

// Root store configuration
const store = {
  state() {
    return {
      appName: 'THBOOKS',
      darkMode: false,
      loading: false,
      error: null,
      notification: null,
      sidebarOpen: true,
      snackbar: {
        show: false,
        text: '',
        color: 'success',
        timeout: 3000
      }
    };
  },
  
  mutations: {
    SET_DARK_MODE(state, isDarkMode) {
      state.darkMode = isDarkMode;
    },
    SET_LOADING(state, isLoading) {
      state.loading = isLoading;
    },
    SET_ERROR(state, error) {
      state.error = error;
    },
    CLEAR_ERROR(state) {
      state.error = null;
    },
    SET_NOTIFICATION(state, notification) {
      state.notification = notification;
    },
    CLEAR_NOTIFICATION(state) {
      state.notification = null;
    },
    SET_SIDEBAR_STATE(state, isOpen) {
      state.sidebarOpen = isOpen;
    },
    TOGGLE_SIDEBAR(state) {
      state.sidebarOpen = !state.sidebarOpen;
    },
    SET_SNACKBAR(state, snackbar) {
      if (snackbar === null) {
        state.snackbar.show = false;
      } else {
        state.snackbar = {
          show: true,
          text: snackbar.text || '',
          color: snackbar.color || 'success',
          timeout: snackbar.timeout || 3000
        };
      }
    }
  },
  
  actions: {
    toggleDarkMode({ commit, state }) {
      const newMode = !state.darkMode;
      localStorage.setItem('darkMode', newMode ? 'true' : 'false');
      commit('SET_DARK_MODE', newMode);
    },
    
    toggleSidebar({ commit }) {
      commit('TOGGLE_SIDEBAR');
    },
    
    setSidebarState({ commit }, isOpen) {
      commit('SET_SIDEBAR_STATE', isOpen);
    },
    
    setLoading({ commit }, isLoading) {
      commit('SET_LOADING', isLoading);
    },
    
    setError({ commit }, error) {
      commit('SET_ERROR', error);
    },
    
    clearError({ commit }) {
      commit('CLEAR_ERROR');
    },
    
    setNotification({ commit }, notification) {
      commit('SET_NOTIFICATION', notification);
      
      // Auto-clear notification after 5 seconds if set
      if (notification) {
        setTimeout(() => {
          commit('CLEAR_NOTIFICATION');
        }, 5000);
      }
    },

    showSnackbar({ commit }, options) {
      commit('SET_SNACKBAR', options);
      
      // Auto-hide the snackbar after the timeout
      if (options && options.timeout !== -1) {
        setTimeout(() => {
          commit('SET_SNACKBAR', null);
        }, options.timeout || 3000);
      }
    }
  },
  
  getters: {
    isDarkMode: state => state.darkMode,
    isLoading: state => state.loading,
    error: state => state.error,
    notification: state => state.notification,
    appName: state => state.appName,
    isSidebarOpen: state => state.sidebarOpen,
    snackbar: state => state.snackbar
  },
  
  modules: {
    auth,
    books,
    borrowings,
    categories,
    authors,
    branches,
    inventory,
    notifications
  }
};

export default store;