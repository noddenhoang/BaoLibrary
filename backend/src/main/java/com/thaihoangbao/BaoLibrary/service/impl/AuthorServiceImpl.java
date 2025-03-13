package com.thaihoangbao.BaoLibrary.service.impl;

import com.thaihoangbao.BaoLibrary.dto.AuthorDto;
import com.thaihoangbao.BaoLibrary.entity.Author;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.repository.AuthorRepository;
import com.thaihoangbao.BaoLibrary.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public List<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorDto getAuthorById(Integer id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + id));
        return convertToDto(author);
    }

    @Override
    @Transactional
    public AuthorDto createAuthor(AuthorDto authorDto) {
        Author author = new Author();
        author.setTenTacGia(authorDto.getTenTacGia());
        return convertToDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    public AuthorDto updateAuthor(Integer id, AuthorDto authorDto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + id));
        author.setTenTacGia(authorDto.getTenTacGia());
        return convertToDto(authorRepository.save(author));
    }

    @Override
    @Transactional
    public void deleteAuthor(Integer id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + id));
        authorRepository.delete(author);
    }

    private AuthorDto convertToDto(Author author) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setAuthorId(author.getAuthorId());
        authorDto.setTenTacGia(author.getTenTacGia());
        return authorDto;
    }
}