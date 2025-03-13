package com.thaihoangbao.BaoLibrary.dto;

import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingDTO {
    
    @NotNull(message = "UserId is required")
    private Integer userId;
    
    @NotNull(message = "BranchId is required")
    private Integer branchId;
    
    @NotEmpty(message = "At least one book must be selected")
    private List<BorrowingDetailDTO> borrowingDetails;
    
    // Inner class for book details in a loan
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BorrowingDetailDTO {
        @NotNull(message = "BookId is required")
        private Integer bookId;
        
        @NotNull(message = "Due date is required")
        private Date dueDate;
    }
}
