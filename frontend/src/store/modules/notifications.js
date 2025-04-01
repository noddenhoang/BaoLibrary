// Notifications Store Module
const notifications = {
  namespaced: true,
  
  state: () => ({
    notifications: [],
    unread: 0
  }),
  
  mutations: {
    SET_NOTIFICATIONS(state, notifications) {
      state.notifications = notifications;
      state.unread = notifications.filter(n => !n.read).length;
    },
    
    ADD_NOTIFICATION(state, notification) {
      state.notifications.unshift(notification);
      if (!notification.read) {
        state.unread++;
      }
    },
    
    MARK_AS_READ(state, notificationId) {
      const notification = state.notifications.find(n => n.id === notificationId);
      if (notification && !notification.read) {
        notification.read = true;
        state.unread--;
      }
    },
    
    MARK_ALL_AS_READ(state) {
      state.notifications.forEach(n => {
        n.read = true;
      });
      state.unread = 0;
    }
  },
  
  actions: {
    // Lấy tất cả thông báo
    fetchNotifications({ commit }) {
      // Đây sẽ là một API call trong tương lai
      const mockNotifications = [
        { id: 1, title: 'Thông báo mẫu', message: 'Đây là thông báo mẫu.', read: false, createdAt: new Date() }
      ];
      commit('SET_NOTIFICATIONS', mockNotifications);
    },
    
    // Thêm thông báo mới
    addNotification({ commit }, notification) {
      commit('ADD_NOTIFICATION', {
        ...notification,
        id: Date.now(),
        read: false,
        createdAt: new Date()
      });
    },
    
    // Đánh dấu thông báo là đã đọc
    markAsRead({ commit }, notificationId) {
      commit('MARK_AS_READ', notificationId);
    },
    
    // Đánh dấu tất cả thông báo là đã đọc
    markAllAsRead({ commit }) {
      commit('MARK_ALL_AS_READ');
    }
  },
  
  getters: {
    notifications: state => state.notifications,
    unreadCount: state => state.unread
  }
};

export default notifications; 