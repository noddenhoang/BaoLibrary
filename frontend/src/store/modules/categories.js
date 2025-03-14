// Categories Store Module
const categories = {
  namespaced: true,
  
  state: () => ({
    categories: [],
    loading: false,
    error: null
  }),
  
  mutations: {
    SET_CATEGORIES(state, categories) {
      state.categories = categories;
    },
    
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    
    SET_ERROR(state, error) {
      state.error = error;
    }
  },
  
  actions: {
    async fetchCategories({ commit }) {
      try {
        commit('SET_LOADING', true);
        commit('SET_ERROR', null);
        
        const response = await this.$axios.get('/api/categories');
        commit('SET_CATEGORIES', response.data);
        
        return response.data;
      } catch (error) {
        console.error('Error fetching categories:', error);
        commit('SET_ERROR', error.message || 'Failed to fetch categories');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    }
  },
  
  getters: {
    categories: state => state.categories,
    isLoading: state => state.loading,
    error: state => state.error
  }
};

export default categories;
