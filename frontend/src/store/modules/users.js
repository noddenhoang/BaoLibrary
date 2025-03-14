// Users Store Module
const users = {
  namespaced: true,
  
  state: () => ({
    borrowedBooks: [],
    borrowHistory: [],
    loading: false,
    error: null
  }),
  
  mutations: {
    SET_BORROWED_BOOKS(state, books) {
      state.borrowedBooks = books;
    },
    
    SET_BORROW_HISTORY(state, history) {
      state.borrowHistory = history;
    },
    
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    
    SET_ERROR(state, error) {
      state.error = error;
    }
  },
  
  actions: {
    // Note: These are placeholder actions that will be implemented in future increments
    async fetchBorrowedBooks({ commit }) {
      try {
        commit('SET_LOADING', true);
        
        // This is a placeholder - will be replaced with actual API call
        const mockedBorrowedBooks = Array(3).fill().map((_, index) => ({
          id: index + 1,
          book: {
            id: index + 1,
            title: `Borrowed Book ${index + 1}`,
            author: `Author ${index + 1}`,
            coverImage: 'https://via.placeholder.com/150'
          },
          borrowDate: new Date(Date.now() - (index * 86400000 * 7)).toISOString(),
          dueDate: new Date(Date.now() + ((3 - index) * 86400000 * 7)).toISOString(),
          returnedDate: null,
          status: index === 0 ? 'OVERDUE' : 'ACTIVE'
        }));
        
        // Simulate API delay
        setTimeout(() => {
          commit('SET_BORROWED_BOOKS', mockedBorrowedBooks);
          commit('SET_LOADING', false);
        }, 500);
      } catch (error) {
        commit('SET_ERROR', error.message || 'Failed to fetch borrowed books');
        commit('SET_LOADING', false);
      }
    },
    
    async fetchBorrowHistory({ commit }) {
      try {
        commit('SET_LOADING', true);
        
        // This is a placeholder - will be replaced with actual API call
        const mockedBorrowHistory = Array(5).fill().map((_, index) => ({
          id: index + 1,
          book: {
            id: index + 1,
            title: `History Book ${index + 1}`,
            author: `Author ${index + 1}`,
            coverImage: 'https://via.placeholder.com/150'
          },
          borrowDate: new Date(Date.now() - (index * 86400000 * 30)).toISOString(),
          dueDate: new Date(Date.now() - (index * 86400000 * 30) + (86400000 * 14)).toISOString(),
          returnedDate: new Date(Date.now() - (index * 86400000 * 30) + (86400000 * 10)).toISOString(),
          status: 'RETURNED'
        }));
        
        // Simulate API delay
        setTimeout(() => {
          commit('SET_BORROW_HISTORY', mockedBorrowHistory);
          commit('SET_LOADING', false);
        }, 500);
      } catch (error) {
        commit('SET_ERROR', error.message || 'Failed to fetch borrow history');
        commit('SET_LOADING', false);
      }
    }
  },
  
  getters: {
    borrowedBooks: state => state.borrowedBooks,
    borrowHistory: state => state.borrowHistory,
    isLoading: state => state.loading,
    error: state => state.error,
    overdueBooks: state => state.borrowedBooks.filter(item => item.status === 'OVERDUE')
  }
};

export default users;
