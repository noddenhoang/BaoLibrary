package com.thaihoangbao.BaoLibrary.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.Borrowing;
import com.thaihoangbao.BaoLibrary.entity.User;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Integer> {
    
    // Find all borrowings by user
    List<Borrowing> findByUser(User user);
    
    // Find all borrowings by user ID
    List<Borrowing> findByUserUserId(Integer userId);
    
    // Find borrowings by branch ID
    List<Borrowing> findByBranchBranchId(Integer branchId);
    
    // Find borrowings by date range
    List<Borrowing> findByBorrowDateBetween(Date startDate, Date endDate);
    
    // Count borrowings by user ID
    long countByUserUserId(Integer userId);
    
    // Find active borrowings (with at least one non-returned book)
    @Query("SELECT b FROM Borrowing b JOIN b.loanDetails ld WHERE b.user.userId = :userId AND ld.returnDate IS NULL")
    List<Borrowing> findActiveBorrowingsByUserId(@Param("userId") Integer userId);

    long countByBorrowDateBetween(Date startDate, Date endDate);
}
