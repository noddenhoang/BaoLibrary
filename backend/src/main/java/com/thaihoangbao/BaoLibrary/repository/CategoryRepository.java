package com.thaihoangbao.BaoLibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByCategoryNameContainingIgnoreCase(String categoryName);
}