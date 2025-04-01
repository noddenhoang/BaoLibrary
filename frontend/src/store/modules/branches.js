import apiService from '@/services/api.service';

export default {
  namespaced: true,

  state: {
    branches: [],
    loading: false,
    error: null
  },

  mutations: {
    SET_BRANCHES(state, branches) {
      state.branches = branches;
    },
    ADD_BRANCH(state, branch) {
      state.branches.push(branch);
    },
    UPDATE_BRANCH(state, updatedBranch) {
      const index = state.branches.findIndex(branch => branch.branchId === updatedBranch.branchId);
      if (index !== -1) {
        state.branches.splice(index, 1, updatedBranch);
      }
    },
    REMOVE_BRANCH(state, branchId) {
      state.branches = state.branches.filter(branch => branch.branchId !== branchId);
    },
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    SET_ERROR(state, error) {
      state.error = error;
    }
  },

  actions: {
    async fetchBranches({ commit }) {
      commit('SET_LOADING', true);
      try {
        const response = await apiService.branches.getAll();
        commit('SET_BRANCHES', response.data);
        return response.data;
      } catch (error) {
        console.error('Error fetching branches:', error);
        commit('SET_ERROR', error.message || 'Error fetching branches');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async fetchBranchById({ commit }, id) {
      commit('SET_LOADING', true);
      try {
        const response = await apiService.branches.getById(id);
        return response.data;
      } catch (error) {
        console.error(`Error fetching branch ${id}:`, error);
        commit('SET_ERROR', error.message || `Error fetching branch ${id}`);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async createBranch({ commit }, branchData) {
      commit('SET_LOADING', true);
      try {
        const response = await apiService.branches.create(branchData);
        commit('ADD_BRANCH', response.data);
        return response.data;
      } catch (error) {
        console.error('Error creating branch:', error);
        commit('SET_ERROR', error.message || 'Error creating branch');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async updateBranch({ commit }, { id, branchData }) {
      commit('SET_LOADING', true);
      try {
        const response = await apiService.branches.update(id, branchData);
        commit('UPDATE_BRANCH', response.data);
        return response.data;
      } catch (error) {
        console.error(`Error updating branch ${id}:`, error);
        commit('SET_ERROR', error.message || `Error updating branch ${id}`);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

    async deleteBranch({ commit }, id) {
      commit('SET_LOADING', true);
      try {
        await apiService.branches.delete(id);
        commit('REMOVE_BRANCH', id);
      } catch (error) {
        console.error(`Error deleting branch ${id}:`, error);
        commit('SET_ERROR', error.message || `Error deleting branch ${id}`);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    }
  },

  getters: {
    branches: state => state.branches,
    loading: state => state.loading,
    error: state => state.error,
    
    // Get branch by ID
    getBranchById: state => id => {
      return state.branches.find(branch => branch.branchId === id);
    }
  }
}; 