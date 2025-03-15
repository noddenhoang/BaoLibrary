/**
 * Utility functions for debugging authentication/authorization issues
 */

export const decodeJwt = (token) => {
  try {
    if (!token) {
      console.log('No token provided');
      return null;
    }
    
    const parts = token.split('.');
    if (parts.length !== 3) {
      console.log('Invalid token format');
      return null;
    }
    
    const base64Url = parts[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(window.atob(base64));
    
    return payload;
  } catch (e) {
    console.error('Error decoding JWT:', e);
    return null;
  }
};

export const checkAuthState = () => {
  // Check local storage
  const token = localStorage.getItem('token');
  const user = JSON.parse(localStorage.getItem('user'));
  
  console.group('Authentication Debug Info');
  console.log('Token exists:', !!token);
  console.log('User data exists:', !!user);
  
  if (token) {
    const decoded = decodeJwt(token);
    if (decoded) {
      console.log('Decoded token:', decoded);
      console.log('Token expiration:', new Date(decoded.exp * 1000).toLocaleString());
      console.log('Role from token:', decoded.role);
      
      if (user) {
        console.log('Role in user object:', user.role);
        console.log('Role matches:', decoded.role === user.role);
      }
    }
  }
  
  // Check axios headers
  if (window.axios) {
    console.log('Authorization header set:', !!window.axios.defaults.headers.common['Authorization']);
  }
  
  console.groupEnd();
};

export default {
  decodeJwt,
  checkAuthState
};
