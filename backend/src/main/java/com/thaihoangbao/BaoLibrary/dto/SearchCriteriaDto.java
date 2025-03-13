package com.thaihoangbao.BaoLibrary.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchCriteriaDto {
    private String keyword;
    private Integer yearFrom;
    private Integer yearTo;
    private List<Integer> authorIds;
    private List<Integer> categoryIds;
}
