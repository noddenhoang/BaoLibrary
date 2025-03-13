package com.thaihoangbao.BaoLibrary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class BookDto {
    private Integer bookId;
    
    @NotBlank(message = "Tên sách không được để trống")
    private String tuaSach;
    
    private String moTa;
    
    @Positive(message = "Năm xuất bản phải là số dương")
    private Integer namXuatBan;
    
    private String hinhAnhSach;
    
    private Set<Integer> authorIds = new HashSet<>();
    
    private Set<Integer> categoryIds = new HashSet<>();
}