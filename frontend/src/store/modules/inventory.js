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
        commit('SET_INVENTORIES', response.data);
        return response.data;
      } catch (error) {
        console.error(`Error fetching inventories for book ${bookId}:`, error);
        commit('SET_ERROR', error.message || `Error fetching inventories for book ${bookId}`);
        throw error;
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