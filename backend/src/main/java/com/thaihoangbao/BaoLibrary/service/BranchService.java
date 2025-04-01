package com.thaihoangbao.BaoLibrary.service;

import java.util.List;

import com.thaihoangbao.BaoLibrary.dto.BranchDto;

public interface BranchService {
    
    /**
     * Lấy tất cả chi nhánh
     * @return danh sách chi nhánh
     */
    List<BranchDto> getAllBranches();
    
    /**
     * Lấy chi nhánh theo ID
     * @param id ID của chi nhánh
     * @return thông tin chi nhánh
     */
    BranchDto getBranchById(Integer id);
    
    /**
     * Tạo mới chi nhánh
     * @param branchDto thông tin chi nhánh
     * @return chi nhánh đã tạo
     */
    BranchDto createBranch(BranchDto branchDto);
    
    /**
     * Cập nhật chi nhánh
     * @param id ID của chi nhánh
     * @param branchDto thông tin cập nhật
     * @return chi nhánh đã cập nhật
     */
    BranchDto updateBranch(Integer id, BranchDto branchDto);
    
    /**
     * Xóa chi nhánh
     * @param id ID của chi nhánh
     */
    void deleteBranch(Integer id);
} 