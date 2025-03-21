package com.thaihoangbao.BaoLibrary.dto;

import lombok.Data;
import java.util.List;

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
}