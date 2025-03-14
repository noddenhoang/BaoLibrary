// Import store modules
import auth from './modules/auth';
import books from './modules/books';
import users from './modules/users';

// Root store configuration
const store = {
  state() {
    return {
      appName: 'Library Management System',
      darkMode: false,
      loading: false,
      error: null,
      notification: null,
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
    }
  },
  
  actions: {
    toggleDarkMode({ commit, state }) {
      const newMode = !state.darkMode;
      localStorage.setItem('darkMode', newMode ? 'true' : 'false');
      commit('SET_DARK_MODE', newMode);
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
    }
  },
  
  getters: {
    isDarkMode: state => state.darkMode,
    isLoading: state => state.loading,
    error: state => state.error,
    notification: state => state.notification,
    appName: state => state.appName
  },
  
  modules: {
    auth,
    books,
    users
  }
};

export default store; 