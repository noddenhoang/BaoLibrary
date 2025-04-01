package com.thaihoangbao.BaoLibrary.service;

import java.util.List;

import com.thaihoangbao.BaoLibrary.dto.InventoryDto;
import com.thaihoangbao.BaoLibrary.entity.Inventory;

public interface InventoryService {
    
    /**
     * Lấy thông tin số lượng sách theo ID sách
     * @param bookId ID của sách
     * @return Danh sách thông tin số lượng theo chi nhánh
     */
    List<InventoryDto> getInventoriesByBookId(Integer bookId);
    
    /**
     * Lấy thông tin số lượng của một sách tại một chi nhánh cụ thể
     * @param bookId ID của sách
     * @param branchId ID của chi nhánh
     * @return Thông tin số lượng
     */
    InventoryDto getInventoryByBookAndBranch(Integer bookId, Integer branchId);
    
    /**
     * Cập nhật số lượng sách theo chi nhánh
     * @param inventoryDto Thông tin số lượng cần cập nhật
     * @return Thông tin sau khi cập nhật
     */
    InventoryDto updateInventory(InventoryDto inventoryDto);
    
    /**
     * Cập nhật số lượng sách cho nhiều chi nhánh
     * @param bookId ID của sách
     * @param inventoryDtos Danh sách thông tin số lượng theo chi nhánh
     * @return Danh sách thông tin số lượng sau khi cập nhật
     */
    List<InventoryDto> updateInventoriesForBook(Integer bookId, List<InventoryDto> inventoryDtos);
    
    /**
     * Chuyển đổi entity thành DTO
     * @param inventory Entity Inventory
     * @return InventoryDto
     */
    InventoryDto convertToDto(Inventory inventory);
    
    /**
     * Kiểm tra sách có sẵn tại chi nhánh cụ thể
     * @param bookId ID của sách
     * @param branchId ID của chi nhánh
     * @return true nếu có sẵn, false nếu không
     */
    boolean isBookAvailableAtBranch(Integer bookId, Integer branchId);
} 