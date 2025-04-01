// Borrowings Store Module
import apiService from '@/services/api.service';

const borrowings = {
  namespaced: true,
  
  state: () => ({
    loading: false,
    error: null,
    activeBorrowings: [],
    borrowingHistory: [],
    currentBorrowing: null
  }),
  
  mutations: {
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    
    SET_ERROR(state, error) {
      state.error = error;
    },
    
    SET_ACTIVE_BORROWINGS(state, borrowings) {
      state.activeBorrowings = borrowings;
    },
    
    SET_BORROWING_HISTORY(state, borrowings) {
      state.borrowingHistory = borrowings;
    },
    
    SET_CURRENT_BORROWING(state, borrowing) {
      state.currentBorrowing = borrowing;
    }
  },
  
  actions: {
    // Tạo mới một phiếu mượn
    async createBorrowing({ commit, dispatch }, borrowingData) {
      try {
        commit('SET_LOADING', true);
        commit('SET_ERROR', null);
        
        const response = await apiService.borrowings.create(borrowingData);
        commit('SET_CURRENT_BORROWING', response.data);
        
        return response.data;
      } catch (error) {
        console.error('Error creating borrowing:', error);
        
        commit('SET_ERROR', error.response?.data?.message || 'Could not create borrowing');
        
        // Throw error to be caught by component
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    // Lấy danh sách phiếu mượn đang hoạt động của người dùng
    async fetchActiveBorrowings({ commit }, userId) {
      try {
        commit('SET_LOADING', true);
        commit('SET_ERROR', null);
        
        const response = await apiService.borrowings.getActiveBorrowings(userId);
        commit('SET_ACTIVE_BORROWINGS', response.data.borrowings);
        
        return response.data;
      } catch (error) {
        console.error('Error fetching active borrowings:', error);
        commit('SET_ERROR', error.response?.data?.message || 'Could not fetch active borrowings');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    // Lấy lịch sử mượn sách của người dùng
    async fetchBorrowingHistory({ commit }, userId) {
      try {
        commit('SET_LOADING', true);
        commit('SET_ERROR', null);
        
        const response = await apiService.borrowings.getBorrowingHistory(userId);
        commit('SET_BORROWING_HISTORY', response.data);
        
        return response.data;
      } catch (error) {
        console.error('Error fetching borrowing history:', error);
        commit('SET_ERROR', error.response?.data?.message || 'Could not fetch borrowing history');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    // Lấy chi tiết một phiếu mượn
    async fetchBorrowingById({ commit }, borrowingId) {
      try {
        commit('SET_LOADING', true);
        commit('SET_ERROR', null);
        
        const response = await apiService.borrowings.getById(borrowingId);
        commit('SET_CURRENT_BORROWING', response.data);
        
        return response.data;
      } catch (error) {
        console.error('Error fetching borrowing details:', error);
        commit('SET_ERROR', error.response?.data?.message || 'Could not fetch borrowing details');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    }
  },
  
  getters: {
    isLoading: state => state.loading,
    error: state => state.error,
    activeBorrowings: state => state.activeBorrowings,
    borrowingHistory: state => state.borrowingHistory,
    currentBorrowing: state => state.currentBorrowing
  }
};

export default borrowings; 