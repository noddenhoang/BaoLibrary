// Books Store Module
const books = {
  namespaced: true,
  
  state: () => ({
    books: [],
    book: null,
    totalBooks: 0,
    loading: false,
    error: null,
    filters: {
      search: '',
      genre: '',
      author: '',
      availability: 'all'
    },
    pagination: {
      page: 1,
      size: 10,
      totalPages: 0
    },
    pagedBooks: {
      content: [],
      totalElements: 0,
      totalPages: 0,
      number: 0,
      size: 10
    }
  }),
  
  mutations: {
    SET_BOOKS(state, { books, totalBooks, totalPages }) {
      state.books = books;
      state.totalBooks = totalBooks;
      state.pagination.totalPages = totalPages;
    },
    
    SET_PAGED_BOOKS(state, pagedBooks) {
      state.pagedBooks = pagedBooks;
    },
    
    SET_BOOK(state, book) {
      state.book = book;
    },
    
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    
    SET_ERROR(state, error) {
      state.error = error;
    },
    
    SET_FILTERS(state, filters) {
      state.filters = { ...state.filters, ...filters };
    },
    
    SET_PAGINATION(state, pagination) {
      state.pagination = { ...state.pagination, ...pagination };
    },
    
    RESET_FILTERS(state) {
      state.filters = {
        search: '',
        genre: '',
        author: '',
        availability: 'all'
      };
    }
  },
  
  actions: {
    async fetchBooks({ commit }, params) {
      try {
        commit('SET_LOADING', true);
        
        // This will be replaced with actual API call in Increment 3
        const response = await this.$axios.get('/api/books', { params });
        
        const pagedBooks = response.data || {
          content: [],
          totalElements: 0,
          totalPages: 0,
          number: 0,
          size: params.pageSize || 10
        };
        
        commit('SET_PAGED_BOOKS', pagedBooks);
        commit('SET_LOADING', false);
        return pagedBooks;
      } catch (error) {
        console.error('Error fetching books:', error);
        commit('SET_ERROR', error.message || 'Failed to fetch books');
        commit('SET_LOADING', false);
        throw error;
      }
    },
    
    async fetchBookById({ commit }, bookId) {
      try {
        commit('SET_LOADING', true);
        
        // This will be replaced with actual API call in Increment 3
        const response = await this.$axios.get(`/api/books/${bookId}`);
        commit('SET_BOOK', response.data);
        commit('SET_LOADING', false);
        return response.data;
      } catch (error) {
        console.error('Error fetching book:', error);
        commit('SET_ERROR', error.message || 'Failed to fetch book details');
        commit('SET_LOADING', false);
        throw error;
      }
    },
    
    async searchBooks({ commit }, params) {
      try {
        commit('SET_LOADING', true);
        
        const response = await this.$axios.get('/api/books/search', { params });
        commit('SET_PAGED_BOOKS', response.data);
        commit('SET_LOADING', false);
        return response.data;
      } catch (error) {
        console.error('Error searching books:', error);
        commit('SET_ERROR', error.message || 'Failed to search books');
        commit('SET_LOADING', false);
        throw error;
      }
    },
    
    async fetchBooksByCategory({ commit }, params) {
      try {
        commit('SET_LOADING', true);
        
        const { categoryId, ...queryParams } = params;
        const response = await this.$axios.get(`/api/books/category/${categoryId}`, { 
          params: queryParams 
        });
        
        commit('SET_PAGED_BOOKS', response.data);
        commit('SET_LOADING', false);
        return response.data;
      } catch (error) {
        console.error('Error fetching books by category:', error);
        commit('SET_ERROR', error.message || 'Failed to fetch books by category');
        commit('SET_LOADING', false);
        throw error;
      }
    },
    
    async fetchBooksByAuthor({ commit }, params) {
      try {
        commit('SET_LOADING', true);
        
        const { authorId, ...queryParams } = params;
        const response = await this.$axios.get(`/api/books/author/${authorId}`, { 
          params: queryParams 
        });
        
        commit('SET_PAGED_BOOKS', response.data);
        commit('SET_LOADING', false);
        return response.data;
      } catch (error) {
        console.error('Error fetching books by author:', error);
        commit('SET_ERROR', error.message || 'Failed to fetch books by author');
        commit('SET_LOADING', false);
        throw error;
      }
    },
    
    setFilters({ commit, dispatch }, filters) {
      commit('SET_FILTERS', filters);
      commit('SET_PAGINATION', { page: 1 }); // Reset to first page when filters change
      dispatch('fetchBooks');
    },
    
    setPagination({ commit, dispatch }, pagination) {
      commit('SET_PAGINATION', pagination);
      dispatch('fetchBooks');
    },
    
    resetFilters({ commit, dispatch }) {
      commit('RESET_FILTERS');
      commit('SET_PAGINATION', { page: 1 });
      dispatch('fetchBooks');
    }
  },
  
  getters: {
    booksList: state => state.books,
    pagedBooks: state => state.pagedBooks,
    currentBook: state => state.book,
    isLoading: state => state.loading,
    error: state => state.error,
    filters: state => state.filters,
    pagination: state => state.pagination,
    totalBooks: state => state.totalBooks
  }
};

export default books;