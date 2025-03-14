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
    meta: { title: 'User Profile - Library Management System' }
  },
  {
    path: '/user/borrowed',
    name: 'BorrowedBooks',
    component: () => import('../views/user/BorrowedBooks.vue'),
    meta: { title: 'Borrowed Books - Library Management System' }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { title: 'Admin Dashboard - Library Management System', admin: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { title: '404 Not Found - Library Management System', public: true }
  }
];

// This function will be called before each route
// to set the document title and handle authentication 
// Will be implemented in increment 2
// const beforeEach = (to, from, next) => {
//   // Set document title
//   document.title = to.meta.title || 'Library Management System';
//   // Auth logic will be implemented in Increment 2
//   next();
// };

export default routes; 