package com.thaihoangbao.BaoLibrary.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BookDto {
    private Integer bookId;
    
    @NotBlank(message = "Tên sách không được để trống")
    private String tuaSach;
    
    private String moTa;
    
    @Positive(message = "Năm xuất bản phải là số dương")
    private Integer namXuatBan;
    
    private String hinhAnhSach;
    
    @Min(value = 0, message = "Số lượng sách không được âm")
    private Integer soLuong = 0;
    
    private Set<Integer> authorIds = new HashSet<>();
    
    private Set<Integer> categoryIds = new HashSet<>();
    
    // Thêm danh sách số lượng theo chi nhánh
    private List<InventoryDto> inventories;
}