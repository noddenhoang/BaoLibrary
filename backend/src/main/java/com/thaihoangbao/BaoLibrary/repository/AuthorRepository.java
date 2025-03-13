package com.thaihoangbao.BaoLibrary.repository;

import com.thaihoangbao.BaoLibrary.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    List<Author> findByTenTacGiaContainingIgnoreCase(String tenTacGia);
}