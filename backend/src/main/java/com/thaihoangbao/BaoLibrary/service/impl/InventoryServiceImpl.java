package com.thaihoangbao.BaoLibrary.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoangbao.BaoLibrary.dto.InventoryDto;
import com.thaihoangbao.BaoLibrary.entity.Book;
import com.thaihoangbao.BaoLibrary.entity.Branch;
import com.thaihoangbao.BaoLibrary.entity.Inventory;
import com.thaihoangbao.BaoLibrary.entity.Inventory.InventoryId;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.repository.BookRepository;
import com.thaihoangbao.BaoLibrary.repository.BranchRepository;
import com.thaihoangbao.BaoLibrary.repository.InventoryRepository;
import com.thaihoangbao.BaoLibrary.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private BranchRepository branchRepository;
    
    @Override
    public List<InventoryDto> getInventoriesByBookId(Integer bookId) {
        // Kiểm tra sách tồn tại
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Không tìm thấy sách với ID: " + bookId);
        }
        
        // Lấy danh sách inventory và chuyển đổi sang DTO
        return inventoryRepository.findByBookBookId(bookId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDto getInventoryByBookAndBranch(Integer bookId, Integer branchId) {
        Inventory inventory = inventoryRepository.findByBookBookIdAndBranchBranchId(bookId, branchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy thông tin số lượng cho sách ID: " + bookId + " tại chi nhánh ID: " + branchId));
        
        return convertToDto(inventory);
    }

    @Override
    @Transactional
    public InventoryDto updateInventory(InventoryDto inventoryDto) {
        // Lấy thông tin sách và chi nhánh
        Book book = bookRepository.findById(inventoryDto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + inventoryDto.getBookId()));
        
        Branch branch = branchRepository.findById(inventoryDto.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi nhánh với ID: " + inventoryDto.getBranchId()));
        
        // Tìm hoặc tạo mới inventory
        Inventory inventory = inventoryRepository.findByBookBookIdAndBranchBranchId(
                inventoryDto.getBookId(), inventoryDto.getBranchId())
                .orElse(new Inventory());
        
        if (inventory.getId() == null) {
            // Tạo mới
            InventoryId id = new InventoryId(inventoryDto.getBranchId(), inventoryDto.getBookId());
            inventory.setId(id);
            inventory.setBook(book);
            inventory.setBranch(branch);
        }
        
        // Cập nhật thông tin số lượng
        inventory.setTotalCopies(inventoryDto.getTongSoBan());
        inventory.setAvailableCopies(inventoryDto.getSoLuongHienCo());
        
        // Lưu vào database
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        // Cập nhật tổng số lượng sách
        updateTotalBookQuantity(inventoryDto.getBookId());
        
        return convertToDto(savedInventory);
    }

    @Override
    @Transactional
    public List<InventoryDto> updateInventoriesForBook(Integer bookId, List<InventoryDto> inventoryDtos) {
        // Kiểm tra sách tồn tại
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Không tìm thấy sách với ID: " + bookId);
        }
        
        // Cập nhật từng inventory
        List<InventoryDto> updatedInventories = inventoryDtos.stream()
                .map(dto -> {
                    // Đảm bảo bookId đúng
                    dto.setBookId(bookId);
                    return updateInventory(dto);
                })
                .collect(Collectors.toList());
        
        // Cập nhật tổng số lượng sách
        updateTotalBookQuantity(bookId);
        
        return updatedInventories;
    }

    @Override
    public InventoryDto convertToDto(Inventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setBranchId(inventory.getBranch().getBranchId());
        dto.setTenChiNhanh(inventory.getBranch().getTenChiNhanh());
        dto.setBookId(inventory.getBook().getBookId());
        dto.setTongSoBan(inventory.getTotalCopies());
        dto.setSoLuongHienCo(inventory.getAvailableCopies());
        return dto;
    }

    @Override
    public boolean isBookAvailableAtBranch(Integer bookId, Integer branchId) {
        return inventoryRepository.isBookAvailableAtBranch(bookId, branchId);
    }
    
    /**
     * Cập nhật tổng số lượng sách trong bảng Book
     * @param bookId ID của sách
     */
    private void updateTotalBookQuantity(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + bookId));
        
        // Tính tổng số lượng từ tất cả các chi nhánh
        Integer totalQuantity = inventoryRepository.findByBookBookId(bookId)
                .stream()
                .mapToInt(Inventory::getAvailableCopies)
                .sum();
        
        // Cập nhật số lượng sách
        book.setSoLuong(totalQuantity);
        bookRepository.save(book);
    }
} 