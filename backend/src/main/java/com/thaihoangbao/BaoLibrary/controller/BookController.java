package com.thaihoangbao.BaoLibrary.controller;

import com.thaihoangbao.BaoLibrary.dto.BookDto;
import com.thaihoangbao.BaoLibrary.dto.BookResponseDto;
import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    // Tạo sách mới (chỉ admin hoặc manager)
    @PostMapping
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    public ResponseEntity<BookResponseDto> createBook(@Valid @RequestBody BookDto bookDto) {
        return new ResponseEntity<>(bookService.createBook(bookDto), HttpStatus.CREATED);
    }
    
    // Lấy sách theo ID
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }
    
    // Lấy tất cả sách với phân trang và sắp xếp
    @GetMapping
    public PagedResponse<BookResponseDto> getAllBooks(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "tuaSach", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        return bookService.getAllBooks(pageNo, pageSize, sortBy, sortDir);
    }
    
    // Tìm kiếm sách theo từ khóa
    @GetMapping("/search")
    public PagedResponse<BookResponseDto> searchBooks(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return bookService.searchBooks(keyword, pageNo, pageSize);
    }
    
    // Lấy sách theo danh mục
    @GetMapping("/category/{categoryId}")
    public PagedResponse<BookResponseDto> getBooksByCategory(
            @PathVariable(value = "categoryId") Integer categoryId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return bookService.getBooksByCategory(categoryId, pageNo, pageSize);
    }
    
    // Lấy sách theo tác giả
    @GetMapping("/author/{authorId}")
    public PagedResponse<BookResponseDto> getBooksByAuthor(
            @PathVariable(value = "authorId") Integer authorId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return bookService.getBooksByAuthor(authorId, pageNo, pageSize);
    }
    
    // Cập nhật sách (chỉ admin hoặc manager)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    public ResponseEntity<BookResponseDto> updateBook(
            @PathVariable(value = "id") Integer id, 
            @Valid @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDto));
    }
    
    // Xóa sách (chỉ admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> deleteBook(@PathVariable(value = "id") Integer id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}