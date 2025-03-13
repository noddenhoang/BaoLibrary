package com.thaihoangbao.BaoLibrary.repository;

import com.thaihoangbao.BaoLibrary.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    // Tìm sách theo tên (không phân biệt chữ hoa/thường)
    List<Book> findByTuaSachContainingIgnoreCase(String tuaSach);
    
    // Tìm sách theo tên với phân trang
    Page<Book> findByTuaSachContainingIgnoreCase(String tuaSach, Pageable pageable);
    
    // Tìm sách theo danh mục
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.categoryId = :categoryId")
    Page<Book> findByCategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);
    
    // Tìm sách theo tác giả
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.authorId = :authorId")
    Page<Book> findByAuthorId(@Param("authorId") Integer authorId, Pageable pageable);
    
    // Tìm sách theo năm xuất bản
    Page<Book> findByNamXuatBan(Integer namXuatBan, Pageable pageable);
}