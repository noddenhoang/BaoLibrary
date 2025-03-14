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
    console.log('API Request:', config.method.toUpperCase(), config.url, config.data);
    return config;
  },
  error => {
    // Do something with request error
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor to handle common errors
api.interceptors.response.use(
  response => {
    console.log('API Response:', response.status, response.data);
    return response;
  },
  error => {
    const { response } = error;
    
    console.error('API Response Error:', 
      response ? `Status: ${response.status}, Data: ${JSON.stringify(response.data)}` : error.message
    );
    
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
    getByCategory: (categoryId, params) => api.get(`/books/category/${categoryId}`, { params }),
    getByAuthor: (authorId, params) => api.get(`/books/author/${authorId}`, { params }),
    // Admin functions - will be implemented in later increments
    create: (bookData) => api.post('/books', bookData),
    update: (id, bookData) => api.put(`/books/${id}`, bookData),
    delete: (id) => api.delete(`/books/${id}`)
  },
  
  // Categories
  categories: {
    getAll: () => api.get('/categories'),
    getById: (id) => api.get(`/categories/${id}`),
    // Admin functions
    create: (categoryData) => api.post('/categories', categoryData),
    update: (id, categoryData) => api.put(`/categories/${id}`, categoryData),
    delete: (id) => api.delete(`/categories/${id}`)
  },
  
  // Authors
  authors: {
    getAll: () => api.get('/authors'),
    getById: (id) => api.get(`/authors/${id}`),
    // Admin functions
    create: (authorData) => api.post('/authors', authorData),
    update: (id, authorData) => api.put(`/authors/${id}`, authorData),
    delete: (id) => api.delete(`/authors/${id}`)
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
  
  // Generic method for any endpoint
  get: (endpoint, params) => api.get(endpoint, { params }),
  post: (endpoint, data) => api.post(endpoint, data),
  put: (endpoint, data) => api.put(endpoint, data),
  delete: (endpoint) => api.delete(endpoint)
};

export default apiService;
