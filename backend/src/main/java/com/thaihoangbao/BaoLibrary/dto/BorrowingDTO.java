package com.thaihoangbao.BaoLibrary.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingDTO {
    
    @NotNull(message = "ID người dùng không được bỏ trống")
    private Integer userId;
    
    @NotNull(message = "ID chi nhánh không được bỏ trống")
    private Integer branchId;
    
    @NotEmpty(message = "Danh sách sách mượn không được bỏ trống")
    private List<BorrowingDetailDTO> borrowingDetails;
    
    // Inner class for book details in a loan
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BorrowingDetailDTO {
        @NotNull(message = "ID sách không được bỏ trống")
        private Integer bookId;
        
        @NotNull(message = "Số ngày mượn không được bỏ trống")
        @Positive(message = "Số ngày mượn phải lớn hơn 0")
        private Integer rentalDays;
    }
}
