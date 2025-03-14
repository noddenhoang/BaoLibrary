import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store';

// Define routes for the Library Management System
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: 'Home - Library Management System' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: { title: 'Login - Library Management System', public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue'),
    meta: { title: 'Register - Library Management System', public: true }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/auth/ForgotPassword.vue'),
    meta: { title: 'Forgot Password - Library Management System', public: true }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('../views/auth/ResetPassword.vue'),
    meta: { title: 'Reset Password - Library Management System', public: true }
  },
  {
    path: '/catalog',
    name: 'Catalog',
    component: () => import('../views/Catalog.vue'),
    meta: { title: 'Book Catalog - Library Management System' }
  },
  {
    path: '/book/:id',
    name: 'BookDetails',
    component: () => import('../views/BookDetails.vue'),
    meta: { title: 'Book Details - Library Management System' }
  },
  {
    path: '/user/profile',
    name: 'Profile',
    component: () => import('../views/user/Profile.vue'),
    meta: { title: 'User Profile - Library Management System', requiresAuth: true }
  },
  {
    path: '/user/borrowed',
    name: 'BorrowedBooks',
    component: () => import('../views/user/BorrowedBooks.vue'),
    meta: { title: 'Borrowed Books - Library Management System', requiresAuth: true }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { title: 'Admin Dashboard - Library Management System', requiresAuth: true, admin: true }
  },
  {
    path: '/admin/books',
    name: 'BookManagement',
    component: () => import('../views/admin/BookManagement.vue'),
    meta: { title: 'Book Management - Library Management System', requiresAuth: true, admin: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { title: '404 Not Found - Library Management System', public: true }
  }
];

// Create router instance
const router = createRouter({
  history: createWebHistory(),
  routes
});

// Navigation guard to check authentication
router.beforeEach((to, from, next) => {
  // Set document title
  document.title = to.meta.title || 'Library Management System';
  
  const isAuthenticated = store.getters['auth/isAuthenticated'];
  const isAdmin = store.getters['auth/isAdmin'];
  
  // If route requires authentication and user is not authenticated
  if (to.meta.requiresAuth && !isAuthenticated) {
    // Redirect to login page with a return URL
    return next({ 
      name: 'Login', 
      query: { redirect: to.fullPath } 
    });
  }
  
  // If route requires admin role and user is not admin
  if (to.meta.admin && !isAdmin) {
    // Redirect to home page with access denied message
    store.dispatch('setNotification', {
      type: 'error',
      message: 'Bạn không có quyền truy cập trang này'
    });
    return next({ name: 'Home' });
  }
  
  // If user is authenticated and tries to access login/register pages
  if (isAuthenticated && (to.name === 'Login' || to.name === 'Register')) {
    // Redirect to home page
    return next({ name: 'Home' });
  }
  
  // Otherwise, proceed as normal
  next();
});

export default router;
