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
    
    // Tìm tất cả inventory theo sách
    List<Inventory> findByBook(Book book);
    
    // Tìm tất cả inventory theo chi nhánh
    List<Inventory> findByBranch(Branch branch);
    
    // Tìm inventory theo sách và chi nhánh
    @Query("SELECT i FROM Inventory i WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId")
    Optional<Inventory> findByBookIdAndBranchId(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId);
    
    // Tìm theo BookId
    @Query("SELECT i FROM Inventory i WHERE i.book.bookId = :bookId")
    List<Inventory> findByBookBookId(@Param("bookId") Integer bookId);
    
    // Tìm theo BookId và BranchId
    @Query("SELECT i FROM Inventory i WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId")
    Optional<Inventory> findByBookBookIdAndBranchBranchId(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId);
    
    // Giảm số lượng sách trong kho
    @Modifying
    @Query("UPDATE Inventory i SET i.availableCopies = i.availableCopies - :amount WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId AND i.availableCopies >= :amount")
    int decreaseQuantity(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId, @Param("amount") Integer amount);
    
    // Tăng số lượng sách trong kho
    @Modifying
    @Query("UPDATE Inventory i SET i.availableCopies = i.availableCopies + :amount WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId")
    int increaseQuantity(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId, @Param("amount") Integer amount);
    
    // Để tương thích với code cũ
    @Modifying
    @Query("UPDATE Inventory i SET i.availableCopies = i.availableCopies - :quantity WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId AND i.availableCopies >= :quantity")
    int decreaseAvailableCopies(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId, @Param("quantity") Integer quantity);
    
    // Để tương thích với code cũ
    @Modifying
    @Query("UPDATE Inventory i SET i.availableCopies = i.availableCopies + :quantity WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId")
    int increaseAvailableCopies(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId, @Param("quantity") Integer quantity);
    
    // Kiểm tra sách có sẵn tại chi nhánh
    @Query("SELECT CASE WHEN COUNT(i) > 0 AND i.availableCopies > 0 THEN true ELSE false END FROM Inventory i WHERE i.book.bookId = :bookId AND i.branch.branchId = :branchId")
    boolean isBookAvailableAtBranch(@Param("bookId") Integer bookId, @Param("branchId") Integer branchId);
} 