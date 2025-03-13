package com.thaihoangbao.BaoLibrary.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponseDTO {
    private Integer userId;
    private String userName;
    private Date returnDate;
    private BigDecimal totalLateFees;
    private List<ReturnDetailResponseDTO> returnDetails;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnDetailResponseDTO {
        private Integer loanDetailId;
        private Integer bookId;
        private String bookTitle;
        private Date dueDate;
        private Date returnDate;
        private Boolean isLate;
        private BigDecimal lateFee;
        private String bookCondition;
        private String comments;
    }
} 