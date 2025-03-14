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
  }
};

export default apiService;