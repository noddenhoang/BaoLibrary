package com.thaihoangbao.BaoLibrary.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingResponseDTO {
    private Integer loanId;
    private Integer userId;
    private String userName;
    private Integer branchId;
    private String branchName;
    private Date borrowDate;
    private List<BorrowingDetailResponseDTO> borrowingDetails;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BorrowingDetailResponseDTO {
        private Integer loanDetailId;
        private Integer bookId;
        private String bookTitle;
        private Date dueDate;
        private Date returnDate; // null if not yet returned
    }
} 