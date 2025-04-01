package com.thaihoangbao.BaoLibrary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchDto {
    private Integer branchId;
    
    @NotBlank(message = "Tên chi nhánh không được để trống")
    private String tenChiNhanh;
    
    private String diaChi;
    
    private String soDienThoai;
    
    private Integer managerId;
} 