package com.thaihoangbao.BaoLibrary.service;

import com.thaihoangbao.BaoLibrary.dto.BookDto;
import com.thaihoangbao.BaoLibrary.dto.BookResponseDto;
import com.thaihoangbao.BaoLibrary.dto.PagedResponse;

public interface BookService {
    BookResponseDto createBook(BookDto bookDto);
    BookResponseDto getBookById(Integer id);
    PagedResponse<BookResponseDto> getAllBooks(int pageNo, int pageSize, String sortBy, String sortDir);
    PagedResponse<BookResponseDto> searchBooks(String keyword, int pageNo, int pageSize);
    PagedResponse<BookResponseDto> getBooksByCategory(Integer categoryId, int pageNo, int pageSize);
    PagedResponse<BookResponseDto> getBooksByAuthor(Integer authorId, int pageNo, int pageSize);
    BookResponseDto updateBook(Integer id, BookDto bookDto);
    void deleteBook(Integer id);
}