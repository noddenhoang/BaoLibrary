import axios from 'axios';

// Create axios instance with base configuration
const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Request interceptor for adding the auth token to all requests
apiClient.interceptors.request.use(
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

// Response interceptor for error handling
apiClient.interceptors.response.use(
  response => {
    // Log successful requests for debugging
    console.log(`API Success: ${response.config.method.toUpperCase()} ${response.config.url} (${response.status})`);
    return response;
  },
  error => {
    // Log failed requests with detailed information
    if (error.response) {
      console.error(`API Error: ${error.config?.method?.toUpperCase()} ${error.config?.url} (${error.response.status})`, error.response.data);
    } else if (error.request) {
      console.error('API Error: No response received', error.request);
    } else {
      console.error('API Error:', error.message);
    }
    
    // Handle token expiration (401 errors)
    if (error.response && error.response.status === 401) {
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

const apiService = {
  // Authentication
  auth: {
    login: (credentials) => apiClient.post('/auth/login', credentials),
    register: (userData) => apiClient.post('/auth/register', userData),
    getProfile: () => apiClient.get('/auth/profile'),
    requestPasswordReset: (email) => apiClient.post('/auth/forgot-password', email),
    resetPassword: (resetData) => apiClient.post('/auth/reset-password', resetData)
  },
  
  // Books
  books: {
    getAll: (params) => apiClient.get('/books', { params }),
    getById: (id) => apiClient.get(`/books/${id}`),
    search: (keyword, params) => apiClient.get('/books/search', { params: { keyword, ...params } }),
    getByCategory: (categoryId, params) => apiClient.get(`/books/category/${categoryId}`, { params }),
    getByAuthor: (authorId, params) => apiClient.get(`/books/author/${authorId}`, { params }),
    // Update these operations to ensure proper error handling
    create: (bookData) => apiClient.post('/books', bookData),
    update: (id, bookData) => apiClient.put(`/books/${id}`, bookData),
    delete: (id) => apiClient.delete(`/books/${id}`)
  },

  // Authors API
  authors: {
    getAll: () => apiClient.get('/authors'),
    getById: (id) => apiClient.get(`/authors/${id}`),
    create: (authorData) => apiClient.post('/authors', authorData),
    update: (id, authorData) => apiClient.put(`/authors/${id}`, authorData),
    delete: (id) => apiClient.delete(`/authors/${id}`)
  },

  // Categories API
  categories: {
    getAll: () => apiClient.get('/categories'),
    getById: (id) => apiClient.get(`/categories/${id}`),
    create: (categoryData) => apiClient.post('/categories', categoryData),
    update: (id, categoryData) => apiClient.put(`/categories/${id}`, categoryData),
    delete: (id) => apiClient.delete(`/categories/${id}`)
  },

  // Files service
  files: {
    upload: (formData) => {
      const token = localStorage.getItem('token');
      const config = {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': token ? `Bearer ${token}` : ''
        }
      };
      
      return apiClient.post('/files/upload', formData, config);
    }
  },

  // Debug helper
  debug: {
    checkAuth: () => {
      const token = localStorage.getItem('token');
      const user = JSON.parse(localStorage.getItem('user'));
      
      console.log('Auth debug info:');
      console.log('- Token exists:', !!token);
      console.log('- User exists:', !!user);
      
      if (user) {
        console.log('- User role:', user.role);
        console.log('- Username:', user.username);
      }
      
      if (token) {
        try {
          const base64Url = token.split('.')[1];
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
          const decodedToken = JSON.parse(window.atob(base64));
          console.log('- Token expiration:', new Date(decodedToken.exp * 1000).toLocaleString());
          console.log('- Current time:', new Date().toLocaleString());
          console.log('- Token expired:', decodedToken.exp < Date.now()/1000);
          return !decodedToken.exp || decodedToken.exp > Date.now()/1000;
        } catch (e) {
          console.error('Error decoding token:', e);
          return false;
        }
      }
      
      return false;
    }
  }
};

export default apiService;
