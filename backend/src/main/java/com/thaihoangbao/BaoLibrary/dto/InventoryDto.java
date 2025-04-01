package com.thaihoangbao.BaoLibrary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {
    @NotNull(message = "ID chi nhánh không được để trống")
    private Integer branchId;
    
    private String tenChiNhanh;
    
    @NotNull(message = "ID sách không được để trống")
    private Integer bookId;
    
    @Min(value = 0, message = "Tổng số bản không được âm")
    private Integer tongSoBan;
    
    @Min(value = 0, message = "Số lượng hiện có không được âm")
    private Integer soLuongHienCo;
} 