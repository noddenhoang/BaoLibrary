// Authors Store Module
const authors = {
  namespaced: true,
  
  state: () => ({
    authors: [],
    loading: false,
    error: null
  }),
  
  mutations: {
    SET_AUTHORS(state, authors) {
      state.authors = authors;
    },
    
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    
    SET_ERROR(state, error) {
      state.error = error;
    }
  },
  
  actions: {
    async fetchAuthors({ commit }) {
      try {
        commit('SET_LOADING', true);
        commit('SET_ERROR', null);
        
        const response = await this.$axios.get('/api/authors');
        commit('SET_AUTHORS', response.data);
        
        return response.data;
      } catch (error) {
        console.error('Error fetching authors:', error);
        commit('SET_ERROR', error.message || 'Failed to fetch authors');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    }
  },
  
  getters: {
    authors: state => state.authors,
    isLoading: state => state.loading,
    error: state => state.error
  }
};

export default authors;
