import axios from 'axios';

// Create axios instance with default configurations
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Request interceptor to add auth token to every request
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
      // Log header for debugging
      console.log('Setting Authorization header:', `Bearer ${token.substring(0, 15)}...`);
    }
    return config;
  },
  error => {
    // Do something with request error
    return Promise.reject(error);
  }
);

// Response interceptor to handle common errors
api.interceptors.response.use(
  response => {
    console.log('API Success:', response.config.method.toUpperCase(), response.config.url, response.status);
    return response;
  },
  error => {
    const { response, config } = error;
    
    // Log full error details
    console.error('API Response Error:', {
      url: config?.url,
      method: config?.method?.toUpperCase(),
      status: response?.status,
      statusText: response?.statusText,
      data: response?.data,
      headers: response?.headers
    });
    
    // Permission denied - handle explicitly
    if (response && response.status === 403) {
      console.error('Permission denied: You do not have the required role to perform this action');
      // Display user-friendly message
      window._vm && window._vm.$toast && 
        window._vm.$toast.error('You do not have permission to perform this action. Please contact an administrator.');
    }
    
    // Handle token expiration
    if (response && response.status === 401) {
      // Clear local storage
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      
      // Redirect to login if not already there
      if (window.location.pathname !== '/login') {
        window.location.href = '/login?session=expired';
      }
    }
    
    return Promise.reject(error);
  }
);

// API Service object with methods for API endpoints
const apiService = {
  // Authentication
  auth: {
    login: (credentials) => api.post('/auth/login', credentials),
    register: (userData) => api.post('/auth/register', userData),
    getProfile: () => api.get('/auth/profile'),
    updateProfile: (userData) => api.put('/auth/profile', userData),
    changePassword: (passwordData) => api.post('/auth/change-password', passwordData),
    requestPasswordReset: (email) => api.post('/auth/forgot-password', { email }),
    resetPassword: (resetData) => api.post('/auth/reset-password', resetData)
  },
  
  // Books
  books: {
    getAll: (params) => api.get('/books', { params }),
    getById: (id) => api.get(`/books/${id}`),
    search: (query) => api.get('/books/search', { params: { query } }),
    getByGenre: (genre) => api.get('/books/genre', { params: { genre } }),
    getByAuthor: (author) => api.get('/books/author', { params: { author } }),
    // Admin functions - will be implemented in later increments
    create: (bookData) => api.post('/books', bookData),
    update: (id, bookData) => api.put(`/books/${id}`, bookData),
    delete: (id) => api.delete(`/books/${id}`)
  },
  
  // User borrowing/returns
  borrowing: {
    borrow: (bookId) => api.post('/borrowing/borrow', { bookId }),
    return: (borrowId) => api.post('/borrowing/return', { borrowId }),
    getBorrowed: () => api.get('/borrowing/current'),
    getHistory: () => api.get('/borrowing/history')
  },
  
  // Admin features - will be implemented in later increments
  admin: {
    getUsers: (params) => api.get('/admin/users', { params }),
    getUserById: (id) => api.get(`/admin/users/${id}`),
    updateUser: (id, userData) => api.put(`/admin/users/${id}`, userData),
    deleteUser: (id) => api.delete(`/admin/users/${id}`),
    getStats: () => api.get('/admin/stats'),
    getReports: (params) => api.get('/admin/reports', { params })
  },

  // Add a debug helper to check authorization status
  debug: {
    checkAuth: () => {
      const token = localStorage.getItem('token');
      if (!token) {
        console.log('No token found in localStorage');
        return false;
      }
      
      try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const decodedToken = JSON.parse(window.atob(base64));
        console.log('Token contents:', decodedToken);
        console.log('Token expiration:', new Date(decodedToken.exp * 1000).toLocaleString());
        console.log('Current time:', new Date().toLocaleString());
        console.log('Role from token:', decodedToken.role);
        console.log('User ID from token:', decodedToken.userId);
        return true;
      } catch (e) {
        console.error('Error decoding token:', e);
        return false;
      }
    }
  }
};

// Add a permission checker helper
apiService.debug.hasPermission = (requiredRoles) => {
  try {
    const token = localStorage.getItem('token');
    if (!token) return false;
    
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const decodedToken = JSON.parse(window.atob(base64));
    const userRole = decodedToken.role || 'member';
    
    if (Array.isArray(requiredRoles)) {
      return requiredRoles.includes(userRole);
    }
    return requiredRoles === userRole;
  } catch (e) {
    console.error('Error checking permissions:', e);
    return false;
  }
};

export default apiService;