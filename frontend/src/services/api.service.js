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
  
  // Books API
  books: {
    getAll: (params) => axios.get('/books', { params }),
    getById: (id) => axios.get(`/books/${id}`),
    search: (query, params) => axios.get('/books/search', { params: { ...params, keyword: query } }),
    getByCategory: (categoryId, params) => axios.get(`/books/category/${categoryId}`, { params }),
    getByAuthor: (authorId, params) => axios.get(`/books/author/${authorId}`, { params }),
    create: (bookData) => axios.post('/books', bookData),
    update: (id, bookData) => axios.put(`/books/${id}`, bookData),
    delete: (id) => axios.delete(`/books/${id}`)
  },
  
  // Categories API
  categories: {
    getAll: () => axios.get('/categories'),
    getById: (id) => axios.get(`/categories/${id}`),
    create: (categoryData) => axios.post('/categories', categoryData),
    update: (id, categoryData) => axios.put(`/categories/${id}`, categoryData),
    delete: (id) => axios.delete(`/categories/${id}`)
  },
  
  // Authors API
  authors: {
    getAll: () => axios.get('/authors'),
    getById: (id) => axios.get(`/authors/${id}`),
    create: (authorData) => axios.post('/authors', authorData),
    update: (id, authorData) => axios.put(`/authors/${id}`, authorData),
    delete: (id) => axios.delete(`/authors/${id}`)
  },
  
  // User borrowing/returns
  borrowing: {
    // ...existing code...
  },
  
  // Admin features
  admin: {
    // ...existing code...
  }
};

// Initialize API service when imported
apiService.init();

export default apiService;
