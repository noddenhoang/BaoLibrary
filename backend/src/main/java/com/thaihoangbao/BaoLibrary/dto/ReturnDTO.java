package com.thaihoangbao.BaoLibrary.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnDTO {
    
    @NotNull(message = "UserId is required")
    private Integer userId;
    
    @NotEmpty(message = "At least one loan detail must be returned")
    private List<ReturnDetailDTO> returnDetails;
    
    // Inner class for details of returned items
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnDetailDTO {
        @NotNull(message = "LoanDetailId is required")
        private Integer loanDetailId;
        
        private String bookCondition; // e.g., "good", "damaged", "lost"
        
        private String comments; // Any additional comments about the return
    }
}
