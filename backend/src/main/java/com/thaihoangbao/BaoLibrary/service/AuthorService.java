package com.thaihoangbao.BaoLibrary.service;

import com.thaihoangbao.BaoLibrary.dto.AuthorDto;
import java.util.List;

public interface AuthorService {
    List<AuthorDto> getAllAuthors();
    AuthorDto getAuthorById(Integer id);
    AuthorDto createAuthor(AuthorDto authorDto);
    AuthorDto updateAuthor(Integer id, AuthorDto authorDto);
    void deleteAuthor(Integer id);
}