import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Create axios instance with base configuration
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error.response || error);
    return Promise.reject(error);
  }
);

// Books API
const books = {
  getAll: (params) => apiClient.get('/books', { params }),
  search: (keyword, params) => apiClient.get(`/books/search?keyword=${keyword}`, { params }),
  getById: (id) => apiClient.get(`/books/${id}`),
  getByCategory: (categoryId, params) => apiClient.get(`/books/category/${categoryId}`, { params }),
  getByAuthor: (authorId, params) => apiClient.get(`/books/author/${authorId}`, { params }),
  create: (bookData) => apiClient.post('/books', bookData),
  update: (id, bookData) => apiClient.put(`/books/${id}`, bookData),
  delete: (id) => apiClient.delete(`/books/${id}`)
};

// Authors API
const authors = {
  getAll: () => apiClient.get('/authors'),
  getById: (id) => apiClient.get(`/authors/${id}`),
  create: (authorData) => apiClient.post('/authors', authorData),
  update: (id, authorData) => apiClient.put(`/authors/${id}`),
  delete: (id) => apiClient.delete(`/authors/${id}`)
};

// Categories API
const categories = {
  getAll: () => apiClient.get('/categories'),
  getById: (id) => apiClient.get(`/categories/${id}`),
  create: (categoryData) => apiClient.post('/categories', categoryData),
  update: (id, categoryData) => apiClient.put(`/categories/${id}`),
  delete: (id) => apiClient.delete(`/categories/${id}`)
};

// Auth API
const auth = {
  login: (credentials) => apiClient.post('/auth/login', credentials),
  register: (userData) => apiClient.post('/auth/register', userData),
  getProfile: () => apiClient.get('/auth/profile'),
  requestPasswordReset: (email) => apiClient.post('/auth/forgot-password', email),
  resetPassword: (resetData) => apiClient.post('/auth/reset-password', resetData)
};

// Files API
const files = {
  upload: (formData) => apiClient.post('/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }),
  getDownloadUrl: (fileName) => `${API_URL}/files/download/${fileName}`
};

export default {
  books,
  authors,
  categories,
  auth,
  files,
  client: apiClient
};
