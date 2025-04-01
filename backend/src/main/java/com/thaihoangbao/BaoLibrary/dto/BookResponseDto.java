package com.thaihoangbao.BaoLibrary.dto;

import java.util.List;

import lombok.Data;

@Data
public class BookResponseDto {
    private Integer bookId;
    private String tuaSach;
    private String moTa;
    private Integer namXuatBan;
    private String hinhAnhSach;
    private Integer soLuong;
    private List<AuthorDto> authors;
    private List<CategoryDto> categories;
    private List<InventoryDto> inventories;
}