package com.thaihoangbao.BaoLibrary.service.impl;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoangbao.BaoLibrary.entity.Borrowing;
import com.thaihoangbao.BaoLibrary.entity.LoanDetail;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.entity.Violation;
import com.thaihoangbao.BaoLibrary.repository.BorrowingRepository;
import com.thaihoangbao.BaoLibrary.repository.LoanDetailRepository;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import com.thaihoangbao.BaoLibrary.repository.ViolationRepository;
import com.thaihoangbao.BaoLibrary.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BorrowingRepository borrowingRepository;
    
    @Autowired
    private LoanDetailRepository loanDetailRepository;
    
    @Autowired
    private ViolationRepository violationRepository;
    
    @Override
    @Transactional
    public void sendBorrowingConfirmation(Integer userId, Integer loanId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Borrowing borrowing = borrowingRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
        
        String content = String.format("Xác nhận mượn sách: Bạn đã mượn %d sách từ chi nhánh %s vào ngày %s.",
            borrowing.getLoanDetails().size(),
            borrowing.getBranch().getTenChiNhanh(),
            new SimpleDateFormat("dd/MM/yyyy").format(borrowing.getBorrowDate()));
        
        sendNotification(userId, content, "borrowing_confirmation");
    }

    @Override
    @Transactional
    public void sendReturnConfirmation(Integer userId, Integer returnId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // In a real implementation, you would look up the return entity
        // For now, we'll just send a generic confirmation
        String content = "Xác nhận trả sách: Cảm ơn bạn đã trả sách.";
        
        sendNotification(userId, content, "return_confirmation");
    }

    @Override
    @Transactional
    public void sendDueDateReminder(Integer userId, Integer loanDetailId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        LoanDetail loanDetail = loanDetailRepository.findById(loanDetailId)
            .orElseThrow(() -> new RuntimeException("Loan detail not found with ID: " + loanDetailId));
        
        String bookTitle = loanDetail.getBook().getTuaSach();
        String dueDate = new SimpleDateFormat("dd/MM/yyyy").format(loanDetail.getDueDate());
        
        String content = String.format("Nhắc nhở hạn trả sách: Sách '%s' sẽ đến hạn trả vào ngày %s. Vui lòng trả sách đúng hạn.",
            bookTitle, dueDate);
        
        sendNotification(userId, content, "due_date_reminder");
    }

    @Override
    @Transactional
    public void sendOverdueNotification(Integer userId, Integer loanDetailId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        LoanDetail loanDetail = loanDetailRepository.findById(loanDetailId)
            .orElseThrow(() -> new RuntimeException("Loan detail not found with ID: " + loanDetailId));
        
        String bookTitle = loanDetail.getBook().getTuaSach();
        String dueDate = new SimpleDateFormat("dd/MM/yyyy").format(loanDetail.getDueDate());
        
        String content = String.format("Sách quá hạn: Sách '%s' đã quá hạn trả từ ngày %s. Vui lòng trả sách ngay để tránh phí phạt tăng thêm.",
            bookTitle, dueDate);
        
        sendNotification(userId, content, "overdue_notification");
    }

    @Override
    @Transactional
    public void sendLateFeeNotification(Integer userId, Integer violationId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Violation violation = violationRepository.findById(violationId)
            .orElseThrow(() -> new RuntimeException("Violation not found with ID: " + violationId));
        
        String bookTitle = violation.getLoanDetail().getBook().getTuaSach();
        String fineAmount = violation.getFineAmount().toString();
        
        String content = String.format("Thông báo phí phạt: Bạn có khoản phí phạt %s cho sách '%s' vì trả muộn. Vui lòng thanh toán phí phạt tại thư viện.",
            fineAmount, bookTitle);
        
        sendNotification(userId, content, "late_fee_notification");
    }

    @Override
    @Transactional
    public void sendNotification(Integer userId, String content, String type) {
        // In a real implementation, you would:
        // 1. Save notification to the database
        // 2. Potentially send real-time notification via WebSocket
        // 3. Potentially send email or SMS
        
        // For now, we'll just log the notification
        System.out.println(String.format("Sending notification to user %d - Type: %s - Content: %s", userId, type, content));
    }
}
