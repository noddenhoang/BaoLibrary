package com.thaihoangbao.BaoLibrary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDto {
    private Integer categoryId;
    
    @NotBlank(message = "Tên danh mục không được để trống")
    private String categoryName;
    
    private String description;
}