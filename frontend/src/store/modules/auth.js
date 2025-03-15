// Authentication Store Module
import apiService from '@/services/api.service';
import router from '@/router';
import axios from 'axios';

const auth = {
  namespaced: true,
  
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user')) || null,
    isAuthenticated: !!localStorage.getItem('token'),
    loading: false,
    error: null,
    resetRequestSent: false,
    resetSuccess: false
  }),
  
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token;
      state.isAuthenticated = !!token;
      
      if (token) {
        localStorage.setItem('token', token);
      } else {
        localStorage.removeItem('token');
      }
    },
    
    SET_USER(state, user) {
      state.user = user;
      
      if (user) {
        localStorage.setItem('user', JSON.stringify(user));
      } else {
        localStorage.removeItem('user');
      }
    },
    
    LOGOUT(state) {
      state.token = null;
      state.user = null;
      state.isAuthenticated = false;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    },
    
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    
    SET_ERROR(state, error) {
      state.error = error;
    },
    
    CLEAR_ERROR(state) {
      state.error = null;
    },
    
    SET_RESET_REQUEST_SENT(state, value) {
      state.resetRequestSent = value;
    },
    
    SET_RESET_SUCCESS(state, value) {
      state.resetSuccess = value;
    }
  },
  
  actions: {
    async login({ commit, dispatch }, authData) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      
      try {
        console.log('Login action with authData:', authData);
        // Make the API call if we don't already have a response
        const responseData = authData.token 
          ? authData 
          : (await apiService.auth.login(authData)).data;
        
        console.log('Login response received:', responseData);
        
        if (!responseData || !responseData.token) {
          throw new Error('No token received from server');
        }
        
        // Decode the JWT token to examine its contents (for debugging)
        try {
          const base64Url = responseData.token.split('.')[1];
          const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
          const decodedToken = JSON.parse(window.atob(base64));
          console.log('Decoded token:', decodedToken);
          console.log('Token role:', decodedToken.role);
        } catch (e) {
          console.error('Error decoding token:', e);
        }
        
        // Save token and user data
        commit('SET_TOKEN', responseData.token);
        commit('SET_USER', { 
          username: responseData.taiKhoan, 
          fullName: responseData.hoTen, 
          role: responseData.role
        });
        
        // Set token in axios default headers for subsequent requests
        axios.defaults.headers.common['Authorization'] = `Bearer ${responseData.token}`;
        
        dispatch('setNotification', { 
          type: 'success', 
          message: 'Đăng nhập thành công!' 
        }, { root: true });
        
        return true;
      } catch (error) {
        console.error('Login error:', error);
        const errorMessage = error.response?.data?.message || 'Đăng nhập thất bại. Vui lòng thử lại.';
        commit('SET_ERROR', errorMessage);
        dispatch('setNotification', { type: 'error', message: errorMessage }, { root: true });
        return false;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async register({ commit, dispatch }, userData) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      
      try {
        const response = await apiService.auth.register(userData);
        console.log('Register response data:', response.data);
        
        // Ensure we're using the correct property names from the response
        const { token, taiKhoan, hoTen, role } = response.data;
        
        commit('SET_TOKEN', token);
        commit('SET_USER', { 
          username: taiKhoan, 
          fullName: hoTen, 
          role: role
        });
        
        // Dispatch to root store to show success notification
        dispatch('setNotification', { 
          type: 'success', 
          message: 'Đăng ký thành công!' 
        }, { root: true });
        
        return true;
      } catch (error) {
        console.error('Register error:', error);
        const errorMessage = error.response?.data?.message || 'Đăng ký thất bại. Vui lòng thử lại.';
        commit('SET_ERROR', errorMessage);
        
        // Dispatch to root store to show error notification
        dispatch('setNotification', { 
          type: 'error', 
          message: errorMessage 
        }, { root: true });
        
        return false;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async requestPasswordReset({ commit, dispatch }, email) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      commit('SET_RESET_REQUEST_SENT', false);
      
      try {
        await apiService.auth.requestPasswordReset(email);
        commit('SET_RESET_REQUEST_SENT', true);
        
        // Dispatch to root store to show success notification
        dispatch('setNotification', { 
          type: 'success', 
          message: 'Yêu cầu đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra email của bạn.' 
        }, { root: true });
        
        return true;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Không thể gửi yêu cầu đặt lại mật khẩu. Vui lòng thử lại.';
        commit('SET_ERROR', errorMessage);
        
        // Dispatch to root store to show error notification
        dispatch('setNotification', { 
          type: 'error', 
          message: errorMessage 
        }, { root: true });
        
        return false;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async resetPassword({ commit, dispatch }, resetData) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      commit('SET_RESET_SUCCESS', false);
      
      try {
        await apiService.auth.resetPassword(resetData);
        commit('SET_RESET_SUCCESS', true);
        
        // Dispatch to root store to show success notification
        dispatch('setNotification', { 
          type: 'success', 
          message: 'Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập bằng mật khẩu mới.' 
        }, { root: true });
        
        return true;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Không thể đặt lại mật khẩu. Vui lòng thử lại.';
        commit('SET_ERROR', errorMessage);
        
        // Dispatch to root store to show error notification
        dispatch('setNotification', { 
          type: 'error', 
          message: errorMessage 
        }, { root: true });
        
        return false;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async fetchUserProfile({ commit, dispatch }) {
      commit('SET_LOADING', true);
      
      try {
        const response = await apiService.auth.getProfile();
        const userData = response.data;
        
        // Update user data in state
        commit('SET_USER', {
          username: userData.taiKhoan,
          fullName: userData.hoTen,
          email: userData.email,
          phone: userData.soDienThoai,
          address: userData.diaChi,
          role: userData.role,
          status: userData.trangThai,
          registrationDate: userData.ngayDangKy
        });
        
        return userData;
      } catch (error) {
        const errorMessage = error.response?.data?.message || 'Không thể tải thông tin người dùng.';
        
        // If 401 Unauthorized, logout the user
        if (error.response?.status === 401) {
          dispatch('logout');
          router.push('/login');
        }
        
        // Dispatch to root store to show error notification
        dispatch('setNotification', { 
          type: 'error', 
          message: errorMessage 
        }, { root: true });
        
        return null;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    logout({ commit, dispatch }) {
      commit('LOGOUT');
      
      // Dispatch to root store to show success notification
      dispatch('setNotification', { 
        type: 'success', 
        message: 'Đăng xuất thành công!' 
      }, { root: true });
      
      // Redirect to login page
      router.push('/login');
    },
    
    checkAuth({ state, commit }) {
      // Check if token exists and is not expired
      const token = localStorage.getItem('token');
      const user = JSON.parse(localStorage.getItem('user'));
      
      if (!token || !user) {
        commit('LOGOUT');
        return false;
      }
      
      // Optional: Add token expiration check
      try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const decodedToken = JSON.parse(window.atob(base64));
        
        // Check if token is expired
        const currentTime = Date.now() / 1000;
        if (decodedToken.exp && decodedToken.exp < currentTime) {
          console.log('Token expired');
          commit('LOGOUT');
          return false;
        }
        
        // Log user role from token for debugging
        console.log('Role from token:', decodedToken.role);
        console.log('Role from state:', state.user?.role);
        
        // Update user role if needed
        if (decodedToken.role && user.role !== decodedToken.role) {
          commit('SET_USER', { 
            ...user,
            role: decodedToken.role 
          });
        }
      } catch (e) {
        console.error('Error decoding or validating token:', e);
      }
      
      // Set token in axios default headers
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      return state.isAuthenticated;
    }
  },
  
  getters: {
    isAuthenticated: state => state.isAuthenticated,
    user: state => state.user,
    token: state => state.token,
    // Fix the role checks to be more robust
    isAdmin: state => state.user && (state.user.role === 'admin' || state.user.role === 'ADMIN'),
    isManager: state => {
      if (!state.user) return false;
      const role = state.user.role.toLowerCase();
      return role === 'admin' || role === 'manager';
    },
    loading: state => state.loading,
    error: state => state.error,
    resetRequestSent: state => state.resetRequestSent,
    resetSuccess: state => state.resetSuccess
  }
};

export default auth;
