package com.thaihoangbao.BaoLibrary.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoangbao.BaoLibrary.dto.InventoryDto;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.service.InventoryService;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    
    /**
     * Lấy thông tin số lượng sách theo ID sách (sử dụng POST)
     * @param requestBody Map chứa bookId
     * @return Danh sách thông tin số lượng theo chi nhánh
     */
    @PostMapping("/book")
    public ResponseEntity<List<InventoryDto>> getInventoriesByBookIdPost(@RequestBody Map<String, Integer> requestBody) {
        try {
            Integer bookId = requestBody.get("bookId");
            if (bookId == null) {
                System.err.println("Invalid request: bookId is missing in request body");
                return ResponseEntity.badRequest().build();
            }
            
            System.out.println("POST API called: getInventoriesByBookIdPost for bookId: " + bookId);
            List<InventoryDto> inventories = inventoryService.getInventoriesByBookId(bookId);
            System.out.println("Found " + inventories.size() + " inventory records");
            
            // In ra chi tiết từng inventory để kiểm tra giá trị
            for (int i = 0; i < inventories.size(); i++) {
                InventoryDto inv = inventories.get(i);
                System.out.println("Inventory " + i + ": " +
                    "branchId=" + inv.getBranchId() + ", " +
                    "tenChiNhanh=" + inv.getTenChiNhanh() + ", " +
                    "bookId=" + inv.getBookId() + ", " +
                    "tongSoBan=" + inv.getTongSoBan() + ", " +
                    "soLuongHienCo=" + inv.getSoLuongHienCo());
            }
            
            return ResponseEntity.ok(inventories);
        } catch (ResourceNotFoundException e) {
            // Nếu không tìm thấy sách, trả về danh sách rỗng thay vì lỗi
            System.err.println("Book not found: " + e.getMessage());
            return ResponseEntity.ok(List.of()); // Trả về danh sách rỗng
        } catch (Exception e) {
            // Log lỗi khác và trả về danh sách rỗng
            System.err.println("Error in getInventoriesByBookIdPost: " + e.getMessage());
            e.printStackTrace(); // In stack trace cho debug
            return ResponseEntity.ok(List.of()); // Trả về danh sách rỗng
        }
    }
    
    /**
     * Lấy thông tin số lượng sách theo ID sách
     * @param bookId ID của sách
     * @return Danh sách thông tin số lượng theo chi nhánh
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<InventoryDto>> getInventoriesByBookId(@PathVariable Integer bookId) {
        try {
            System.out.println("API called: getInventoriesByBookId for bookId: " + bookId);
            List<InventoryDto> inventories = inventoryService.getInventoriesByBookId(bookId);
            System.out.println("Returning inventories: " + inventories);
            
            // In ra chi tiết từng inventory để kiểm tra giá trị
            for (int i = 0; i < inventories.size(); i++) {
                InventoryDto inv = inventories.get(i);
                System.out.println("Inventory " + i + ": " +
                    "branchId=" + inv.getBranchId() + ", " +
                    "tenChiNhanh=" + inv.getTenChiNhanh() + ", " +
                    "bookId=" + inv.getBookId() + ", " +
                    "tongSoBan=" + inv.getTongSoBan() + ", " +
                    "soLuongHienCo=" + inv.getSoLuongHienCo());
            }
            
            return ResponseEntity.ok(inventories);
        } catch (ResourceNotFoundException e) {
            // Nếu không tìm thấy sách, trả về danh sách rỗng thay vì lỗi
            System.err.println("Không tìm thấy sách với ID " + bookId + ": " + e.getMessage());
            return ResponseEntity.ok(List.of()); // Trả về danh sách rỗng
        } catch (Exception e) {
            // Log lỗi khác và trả về danh sách rỗng
            System.err.println("Lỗi khi lấy inventories cho book ID " + bookId + ": " + e.getMessage());
            return ResponseEntity.ok(List.of()); // Trả về danh sách rỗng
        }
    }
    
    /**
     * Lấy thông tin số lượng của một sách tại một chi nhánh cụ thể
     * @param bookId ID của sách
     * @param branchId ID của chi nhánh
     * @return Thông tin số lượng
     */
    @GetMapping("/book/{bookId}/branch/{branchId}")
    public ResponseEntity<InventoryDto> getInventoryByBookAndBranch(
            @PathVariable Integer bookId, 
            @PathVariable Integer branchId) {
        try {
            return ResponseEntity.ok(inventoryService.getInventoryByBookAndBranch(bookId, branchId));
        } catch (ResourceNotFoundException e) {
            // Trả về null nếu không tìm thấy
            System.err.println("Không tìm thấy inventory cho book ID " + bookId + " và branch ID " + branchId + ": " + e.getMessage());
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy inventory cho book ID " + bookId + " và branch ID " + branchId + ": " + e.getMessage());
            return ResponseEntity.ok(null);
        }
    }
    
    /**
     * Cập nhật số lượng sách theo chi nhánh
     * @param inventoryDto Thông tin số lượng cần cập nhật
     * @return Thông tin sau khi cập nhật
     */
    @PutMapping("/update")
    public ResponseEntity<InventoryDto> updateInventory(@RequestBody InventoryDto inventoryDto) {
        return ResponseEntity.ok(inventoryService.updateInventory(inventoryDto));
    }
    
    /**
     * Cập nhật số lượng sách cho nhiều chi nhánh
     * @param bookId ID của sách
     * @param inventoryDtos Danh sách thông tin số lượng theo chi nhánh
     * @return Danh sách thông tin số lượng sau khi cập nhật
     */
    @PutMapping("/book/{bookId}/update-all")
    public ResponseEntity<List<InventoryDto>> updateInventoriesForBook(
            @PathVariable Integer bookId, 
            @RequestBody List<InventoryDto> inventoryDtos) {
        return ResponseEntity.ok(inventoryService.updateInventoriesForBook(bookId, inventoryDtos));
    }
    
    /**
     * Kiểm tra sách có sẵn tại chi nhánh cụ thể
     * @param bookId ID của sách
     * @param branchId ID của chi nhánh
     * @return true nếu có sẵn, false nếu không
     */
    @GetMapping("/check/{bookId}/{branchId}")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Integer bookId, 
            @PathVariable Integer branchId) {
        boolean isAvailable = inventoryService.isBookAvailableAtBranch(bookId, branchId);
        return ResponseEntity.ok(isAvailable);
    }
}