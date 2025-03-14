import axios from 'axios';

const apiService = {
  init(baseURL = 'http://localhost:8080/api') {
    axios.defaults.baseURL = baseURL;
    
    // Add request interceptor to inject JWT token
    axios.interceptors.request.use(
      config => {
        const token = localStorage.getItem('token');
        if (token) {
          config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
      },
      error => {
        return Promise.reject(error);
      }
    );
    
    // Add response interceptor to handle errors
    axios.interceptors.response.use(
      response => response,
      error => {
        // Handle 401 errors by redirecting to login
        if (error.response && error.response.status === 401) {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          // Use window.location instead of router to ensure full page reload
          if (!window.location.href.includes('/login')) {
            window.location.href = '/login';
          }
        }
        return Promise.reject(error);
      }
    );
  },
  
  // Auth API Endpoints
  auth: {
    login(credentials) {
      console.log('Sending login request with credentials:', credentials);
      return axios.post('/auth/login', credentials);
    },
    
    register(userData) {
      // Ensure data format matches backend expectations
      return axios.post('/auth/register', userData);
    },
    
    getProfile() {
      return axios.get('/auth/profile');
    },
    
    requestPasswordReset(email) {
      return axios.post('/auth/forgot-password', { email });
    },
    
    resetPassword(resetData) {
      return axios.post('/auth/reset-password', resetData);
    }
  },
  
  // Other API services can be added here
};

// Initialize API service when imported
apiService.init();

export default apiService;
