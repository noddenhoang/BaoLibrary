package com.thaihoangbao.BaoLibrary.service;

public interface NotificationService {
    
    /**
     * Send borrowing confirmation notification
     */
    void sendBorrowingConfirmation(Integer userId, Integer loanId);
    
    /**
     * Send return confirmation notification
     */
    void sendReturnConfirmation(Integer userId, Integer returnId);
    
    /**
     * Send due date reminder notification
     */
    void sendDueDateReminder(Integer userId, Integer loanDetailId);
    
    /**
     * Send overdue notification
     */
    void sendOverdueNotification(Integer userId, Integer loanDetailId);
    
    /**
     * Send late fee notification
     */
    void sendLateFeeNotification(Integer userId, Integer violationId);
    
    /**
     * Send general notification to user
     */
    void sendNotification(Integer userId, String content, String type);
}
