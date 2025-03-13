package com.thaihoangbao.BaoLibrary.service;

import com.thaihoangbao.BaoLibrary.dto.BookResponseDto;
import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.dto.SearchCriteriaDto;

public interface SearchService {
    PagedResponse<BookResponseDto> advancedSearch(SearchCriteriaDto criteria, int pageNo, int pageSize);
}
