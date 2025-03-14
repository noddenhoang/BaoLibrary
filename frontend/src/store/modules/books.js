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
    }
  }),
  
  mutations: {
    SET_BOOKS(state, { books, totalBooks, totalPages }) {
      state.books = books;
      state.totalBooks = totalBooks;
      state.pagination.totalPages = totalPages;
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
    // Note: These are placeholder actions that will be implemented in Increment 3
    async fetchBooks({ commit, state }) {
      try {
        commit('SET_LOADING', true);
        
        // This is a placeholder - will be replaced with actual API call in Increment 3
        const mockedBooks = Array(10).fill().map((_, index) => ({
          id: index + 1,
          title: `Book Title ${index + 1}`,
          author: `Author ${index + 1}`,
          isbn: `978-3-16-1484${index}0-0`,
          publishedYear: 2010 + index,
          genre: index % 2 === 0 ? 'Fiction' : 'Non-Fiction',
          available: index % 3 !== 0
        }));
        
        const { page, size } = state.pagination;
        const totalBooks = 100; // Mock total
        const totalPages = Math.ceil(totalBooks / size);
        
        // Simulate API delay
        setTimeout(() => {
          commit('SET_BOOKS', { 
            books: mockedBooks, 
            totalBooks, 
            totalPages 
          });
          commit('SET_LOADING', false);
        }, 500);
      } catch (error) {
        commit('SET_ERROR', error.message || 'Failed to fetch books');
        commit('SET_LOADING', false);
      }
    },
    
    async fetchBookById({ commit }, bookId) {
      try {
        commit('SET_LOADING', true);
        
        // This is a placeholder - will be replaced with actual API call in Increment 3
        const mockedBook = {
          id: bookId,
          title: `Book Title ${bookId}`,
          author: `Author ${bookId}`,
          isbn: `978-3-16-1484${bookId}0-0`,
          publishedYear: 2010 + parseInt(bookId),
          genre: parseInt(bookId) % 2 === 0 ? 'Fiction' : 'Non-Fiction',
          available: parseInt(bookId) % 3 !== 0,
          description: 'This is a detailed description of the book. It contains information about the plot, characters, and themes explored in the book.',
          coverImage: 'https://via.placeholder.com/150',
          totalCopies: 5,
          availableCopies: parseInt(bookId) % 3 !== 0 ? 2 : 0
        };
        
        // Simulate API delay
        setTimeout(() => {
          commit('SET_BOOK', mockedBook);
          commit('SET_LOADING', false);
        }, 500);
      } catch (error) {
        commit('SET_ERROR', error.message || 'Failed to fetch book details');
        commit('SET_LOADING', false);
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
    currentBook: state => state.book,
    isLoading: state => state.loading,
    error: state => state.error,
    filters: state => state.filters,
    pagination: state => state.pagination,
    totalBooks: state => state.totalBooks
  }
};

export default books; 