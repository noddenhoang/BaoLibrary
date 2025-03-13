package com.thaihoangbao.BaoLibrary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.Book;
import com.thaihoangbao.BaoLibrary.entity.Branch;
import com.thaihoangbao.BaoLibrary.entity.Inventory;
import com.thaihoangbao.BaoLibrary.entity.Inventory.InventoryId;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, InventoryId> {
    
    // Find by book and branch
    Optional<Inventory> findByBookAndBranch(Book book, Branch branch);
    
    // Find by book ID and branch ID
    Optional<Inventory> findByBookBookIdAndBranchBranchId(Integer bookId, Integer branchId);
    
    // Find all inventory for a specific book
    List<Inventory> findByBookBookId(Integer bookId);
    
    // Find all inventory for a specific branch
    List<Inventory> findByBranchBranchId(Integer branchId);
    
    // Find books with available copies at a branch
    List<Inventory> findByBranchBranchIdAndAvailableCopiesGreaterThan(Integer branchId, Integer minAvailable);
    
    // Update available copies
    @Modifying
    @Query("UPDATE Inventory i SET i.availableCopies = i.availableCopies - :quantity WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId AND i.availableCopies >= :quantity")
    int decreaseAvailableCopies(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId, @Param("quantity") Integer quantity);
    
    // Increase available copies (for returns)
    @Modifying
    @Query("UPDATE Inventory i SET i.availableCopies = i.availableCopies + :quantity WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId")
    int increaseAvailableCopies(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId, @Param("quantity") Integer quantity);
    
    // Check if a book is available at a branch
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Inventory i WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId AND i.availableCopies > 0")
    boolean isBookAvailableAtBranch(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId);
} 