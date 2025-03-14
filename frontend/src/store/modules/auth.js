// Authentication Store Module
const auth = {
  namespaced: true,
  
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user')) || null,
    isAuthenticated: !!localStorage.getItem('token')
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
    }
  },
  
  actions: {
    // Note: These are placeholder actions that will be implemented in Increment 2
    async login({ commit }, credentials) {
      // Will be implemented in Increment 2
      console.log('Login attempted with:', credentials);
      
      // Placeholder for now - will be replaced with actual API call
      commit('SET_TOKEN', 'dummy-token');
      commit('SET_USER', { 
        id: 1, 
        username: credentials.username, 
        email: 'user@example.com',
        role: 'USER'
      });
      
      return true;
    },
    
    async register({ commit }, userData) {
      // Will be implemented in Increment 2
      console.log('Register attempted with:', userData);
      
      // Placeholder for now - will be replaced with actual API call
      return true;
    },
    
    logout({ commit }) {
      commit('LOGOUT');
    },
    
    checkAuth({ state }) {
      return state.isAuthenticated;
    }
  },
  
  getters: {
    isAuthenticated: state => state.isAuthenticated,
    user: state => state.user,
    token: state => state.token,
    isAdmin: state => state.user && state.user.role === 'ADMIN'
  }
};

export default auth; 