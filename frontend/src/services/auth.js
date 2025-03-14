import api from './api';
import store from '@/store';

// Authentication Service
const authService = {
  /**
   * Check if the user is authenticated
   * @returns {boolean} Authentication status
   */
  isAuthenticated() {
    return store.getters['auth/isAuthenticated'];
  },
  
  /**
   * Check if the user has admin role
   * @returns {boolean} Admin status
   */
  isAdmin() {
    return store.getters['auth/isAdmin'];
  },
  
  /**
   * Get the current user
   * @returns {Object|null} User object or null if not authenticated
   */
  getUser() {
    return store.getters['auth/user'];
  },
  
  /**
   * Get the auth token
   * @returns {string|null} Auth token or null if not authenticated
   */
  getToken() {
    return store.getters['auth/token'];
  },
  
  /**
   * Log in a user with username and password
   * @param {Object} credentials - User credentials
   * @param {string} credentials.username - Username
   * @param {string} credentials.password - Password
   * @returns {Promise} Promise that resolves with user data
   */
  async login(credentials) {
    try {
      // This is a placeholder for now - will be replaced with actual API call in Increment 2
      console.log('Login with credentials:', credentials);
      
      // For now, we'll use the store action (which is also a placeholder)
      const success = await store.dispatch('auth/login', credentials);
      return success;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  },
  
  /**
   * Register a new user
   * @param {Object} userData - User registration data
   * @returns {Promise} Promise that resolves with success status
   */
  async register(userData) {
    try {
      // This is a placeholder for now - will be replaced with actual API call in Increment 2
      console.log('Register with data:', userData);
      
      // For now, we'll use the store action (which is also a placeholder)
      const success = await store.dispatch('auth/register', userData);
      return success;
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  },
  
  /**
   * Log out the current user
   */
  logout() {
    store.dispatch('auth/logout');
  },
  
  /**
   * Request a password reset
   * @param {string} email - User email
   * @returns {Promise} Promise that resolves with success status
   */
  async requestPasswordReset(email) {
    try {
      // This is a placeholder for now - will be replaced with actual API call in Increment 2
      console.log('Password reset requested for:', email);
      return true;
    } catch (error) {
      console.error('Password reset request error:', error);
      throw error;
    }
  },
  
  /**
   * Reset a password with a token
   * @param {Object} resetData - Password reset data
   * @param {string} resetData.token - Reset token
   * @param {string} resetData.password - New password
   * @returns {Promise} Promise that resolves with success status
   */
  async resetPassword(resetData) {
    try {
      // This is a placeholder for now - will be replaced with actual API call in Increment 2
      console.log('Password reset with data:', resetData);
      return true;
    } catch (error) {
      console.error('Password reset error:', error);
      throw error;
    }
  }
};

export default authService; 