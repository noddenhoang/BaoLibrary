// Authentication Store Module
import apiService from '@/services/api.service';
import router from '@/router';

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
    async login({ commit, dispatch }, credentials) {
      commit('SET_LOADING', true);
      commit('CLEAR_ERROR');
      
      try {
        const response = await apiService.auth.login(credentials);
        console.log('Dữ liệu phản hồi đăng nhập:', response.data);
        
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
          message: 'Đăng nhập thành công!' 
        }, { root: true });
        
        return true;
      } catch (error) {
        console.error('Lỗi đăng nhập:', error);
        const errorMessage = error.response?.data?.message || 'Đăng nhập thất bại. Vui lòng thử lại.';
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
    
    checkAuth({ state }) {
      return state.isAuthenticated;
    }
  },
  
  getters: {
    isAuthenticated: state => state.isAuthenticated,
    user: state => state.user,
    token: state => state.token,
    isAdmin: state => state.user && state.user.role === 'admin',
    isManager: state => state.user && (state.user.role === 'manager' || state.user.role === 'admin'),
    loading: state => state.loading,
    error: state => state.error,
    resetRequestSent: state => state.resetRequestSent,
    resetSuccess: state => state.resetSuccess
  }
};

export default auth;
