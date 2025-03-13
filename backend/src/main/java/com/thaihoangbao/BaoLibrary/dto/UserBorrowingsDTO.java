package com.thaihoangbao.BaoLibrary.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBorrowingsDTO {
    private Integer userId;
    private String userName;
    private List<LoanDTO> loans;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanDTO {
        private Integer loanId;
        private Date borrowDate;
        private Integer branchId;
        private String branchName;
        private List<LoanDetailDTO> loanDetails;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanDetailDTO {
        private Integer loanDetailId;
        private Integer bookId;
        private String bookTitle;
        private String bookImage;
        private Date dueDate;
        private Boolean isOverdue;
        private Long daysRemaining; // negative if overdue
    }
} 