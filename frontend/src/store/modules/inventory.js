import apiService from '@/services/api.service';

export default {
  namespaced: true,

  state: {
    inventories: [],
    loading: false,
    error: null
  },

  mutations: {
    SET_INVENTORIES(state, inventories) {
      state.inventories = inventories;
    },
    UPDATE_INVENTORY(state, updatedInventory) {
      const index = state.inventories.findIndex(
        inv => inv.bookId === updatedInventory.bookId && inv.branchId === updatedInventory.branchId
      );
      if (index !== -1) {
        state.inventories.splice(index, 1, updatedInventory);
      } else {
        state.inventories.push(updatedInventory);
      }
    },
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    SET_ERROR(state, error) {
      state.error = error;
    }
  },

  actions: {
    async fetchInventoriesByBookId({ commit }, bookId) {
      commit('SET_LOADING', true);
      try {
        const response = await apiService.inventory.getByBookId(bookId);
        // Kiểm tra dữ liệu response
        console.log('Raw inventory response:', JSON.stringify(response.data, null, 2));
        
        // Xử lý và chuẩn hóa dữ liệu
        const processedInventories = (response.data || []).map(inv => {
          return {
            ...inv,
            // Đảm bảo soLuongHienCo luôn có giá trị dù API trả về tên trường nào
            soLuongHienCo: inv.soLuongHienCo !== undefined ? inv.soLuongHienCo :
                           inv.availableCopies !== undefined ? inv.availableCopies : 0,
            // Đảm bảo tongSoBan luôn có giá trị
            tongSoBan: inv.tongSoBan !== undefined ? inv.tongSoBan :
                       inv.totalCopies !== undefined ? inv.totalCopies : 0
          };
        });
        
        console.log('Processed inventories:', JSON.stringify(processedInventories, null, 2));
        commit('SET_INVENTORIES', processedInventories);
        return processedInventories;
      } catch (error) {
        console.error(`Error fetching inventories for book ${bookId}:`, error);
        commit('SET_ERROR', error.message || `Error fetching inventories for book ${bookId}`);
        commit('SET_INVENTORIES', []);
        return [];
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchInventoryByBookAndBranch({ commit }, { bookId, branchId }) {
      commit('SET_LOADING', true);
      try {
        const response = await apiService.inventory.getByBookAndBranch(bookId, branchId);
        return response.data;
      } catch (error) {
        console.error(`Error fetching inventory for book ${bookId} at branch ${branchId}:`, error);
        commit('SET_ERROR', error.message || `Error fetching inventory details`);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async updateInventory({ commit }, inventoryData) {
      commit('SET_LOADING', true);
      try {
        const response = await apiService.inventory.update(inventoryData);
        commit('UPDATE_INVENTORY', response.data);
        return response.data;
      } catch (error) {
        console.error('Error updating inventory:', error);
        commit('SET_ERROR', error.message || 'Error updating inventory');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async updateInventoriesForBook({ commit }, { bookId, inventories }) {
      commit('SET_LOADING', true);
      try {
        const response = await apiService.inventory.updateAll(bookId, inventories);
        commit('SET_INVENTORIES', response.data);
        return response.data;
      } catch (error) {
        console.error(`Error updating inventories for book ${bookId}:`, error);
        commit('SET_ERROR', error.message || `Error updating inventories for book ${bookId}`);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async checkAvailability({ commit }, { bookId, branchId }) {
      try {
        const response = await apiService.inventory.checkAvailability(bookId, branchId);
        return response.data;
      } catch (error) {
        console.error(`Error checking availability for book ${bookId} at branch ${branchId}:`, error);
        commit('SET_ERROR', error.message || `Error checking book availability`);
        throw error;
      }
    },

    async fetchInventoriesDirectly({ commit }, bookId) {
      commit('SET_LOADING', true);
      try {
        console.log('Fetching inventories directly from API for book ID:', bookId);
        const response = await apiService.inventory.getByBookId(bookId);
        console.log('Direct API response:', JSON.stringify(response.data, null, 2));
        
        if (!response.data || !Array.isArray(response.data)) {
          console.warn('API did not return array data:', response.data);
          return [];
        }
        
        // Xử lý và chuẩn hóa dữ liệu
        const processedInventories = response.data.map(inv => {
          return {
            ...inv,
            // Đảm bảo soLuongHienCo luôn có giá trị dù API trả về tên trường nào
            soLuongHienCo: inv.soLuongHienCo !== undefined ? inv.soLuongHienCo :
                           inv.availableCopies !== undefined ? inv.availableCopies : 0,
            // Đảm bảo tongSoBan luôn có giá trị
            tongSoBan: inv.tongSoBan !== undefined ? inv.tongSoBan :
                       inv.totalCopies !== undefined ? inv.totalCopies : 0
          };
        });
        
        console.log('Processed direct inventories:', JSON.stringify(processedInventories, null, 2));
        return processedInventories;
      } catch (error) {
        console.error('Error fetching inventories directly:', error);
        return [];
      } finally {
        commit('SET_LOADING', false);
      }
    }
  },

  getters: {
    inventories: state => state.inventories,
    loading: state => state.loading,
    error: state => state.error,
    
    // Get inventory by book ID and branch ID
    getInventoryByBookAndBranch: state => (bookId, branchId) => {
      return state.inventories.find(inv => inv.bookId === bookId && inv.branchId === branchId);
    },
    
    // Get all inventories for a book
    getInventoriesByBook: state => bookId => {
      return state.inventories.filter(inv => inv.bookId === bookId);
    }
  }
}; 