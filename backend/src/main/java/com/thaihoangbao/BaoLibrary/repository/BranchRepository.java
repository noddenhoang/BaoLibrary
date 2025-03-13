package com.thaihoangbao.BaoLibrary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.Branch;
import com.thaihoangbao.BaoLibrary.entity.User;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    
    // Find branch by name
    Optional<Branch> findByTenChiNhanh(String tenChiNhanh);
    
    // Find branches by manager - sửa lại sử dụng tên trường đúng (userId thay vì id)
    List<Branch> findByManagerUserId(Integer managerId);
    
    // Find branches by manager
    List<Branch> findByManager(User manager);
}