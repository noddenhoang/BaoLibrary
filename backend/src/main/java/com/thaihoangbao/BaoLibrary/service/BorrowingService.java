package com.thaihoangbao.BaoLibrary.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.thaihoangbao.BaoLibrary.dto.BorrowingDTO;
import com.thaihoangbao.BaoLibrary.dto.BorrowingResponseDTO;
import com.thaihoangbao.BaoLibrary.dto.UserBorrowingsDTO;

public interface BorrowingService {
    
    /**
     * Create a new borrowing (loan) with multiple books
     */
    BorrowingResponseDTO createBorrowing(BorrowingDTO borrowingDTO);
    
    /**
     * Get a borrowing by ID
     */
    BorrowingResponseDTO getBorrowingById(Integer borrowingId);
    
    /**
     * Get all active borrowings for a user (borrowings with at least one non-returned book)
     */
    UserBorrowingsDTO getActiveBorrowingsForUser(Integer userId);
    
    /**
     * Get all borrowings for a user (including completed ones)
     */
    List<BorrowingResponseDTO> getAllBorrowingsForUser(Integer userId);
    
    /**
     * Get statistics on borrowings for a date range
     */
    long countBorrowingsByDateRange(Date startDate, Date endDate);
    
    /**
     * Check if a user has reached the maximum allowed borrowings
     */
    boolean hasUserReachedMaximumAllowedBorrowings(Integer userId);
    
    /**
     * Check if a book is available for borrowing
     */
    boolean isBookAvailableForBorrowing(Integer bookId, Integer branchId);
    
    /**
     * Tính phí thuê sách dựa trên số ngày thuê
     * @param rentalDays Số ngày thuê
     * @return Phí thuê sách
     */
    BigDecimal calculateRentalFee(Integer rentalDays);
}
