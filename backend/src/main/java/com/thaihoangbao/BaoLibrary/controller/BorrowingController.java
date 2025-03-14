package com.thaihoangbao.BaoLibrary.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoangbao.BaoLibrary.dto.BorrowingDTO;
import com.thaihoangbao.BaoLibrary.dto.BorrowingResponseDTO;
import com.thaihoangbao.BaoLibrary.dto.UserBorrowingsDTO;
import com.thaihoangbao.BaoLibrary.service.BorrowingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    @Autowired
    private BorrowingService borrowingService;
    
    /**
     * Create a new borrowing record
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isCurrentUser(#borrowingDTO.userId)")
    public ResponseEntity<BorrowingResponseDTO> createBorrowing(@Valid @RequestBody BorrowingDTO borrowingDTO) {
        BorrowingResponseDTO response = borrowingService.createBorrowing(borrowingDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get a borrowing by ID
     */
    @GetMapping("/{borrowingId}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isUserOfBorrowing(#borrowingId)")
    public ResponseEntity<BorrowingResponseDTO> getBorrowingById(@PathVariable Integer borrowingId) {
        BorrowingResponseDTO response = borrowingService.getBorrowingById(borrowingId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get active borrowings for a user
     */
    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isCurrentUser(#userId)")
    public ResponseEntity<UserBorrowingsDTO> getActiveBorrowingsForUser(@PathVariable Integer userId) {
        UserBorrowingsDTO response = borrowingService.getActiveBorrowingsForUser(userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all borrowings for a user (including completed ones)
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isCurrentUser(#userId)")
    public ResponseEntity<List<BorrowingResponseDTO>> getAllBorrowingsForUser(@PathVariable Integer userId) {
        List<BorrowingResponseDTO> response = borrowingService.getAllBorrowingsForUser(userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check if a book is available for borrowing
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkBookAvailability(@RequestParam Integer bookId, @RequestParam Integer branchId) {
        boolean isAvailable = borrowingService.isBookAvailableForBorrowing(bookId, branchId);
        return ResponseEntity.ok(isAvailable);
    }
    
    /**
     * Check if a user has reached the maximum allowed borrowings
     */
    @GetMapping("/check-limit")
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isCurrentUser(#userId)")
    public ResponseEntity<Boolean> checkUserBorrowingLimit(@RequestParam Integer userId) {
        boolean hasReachedLimit = borrowingService.hasUserReachedMaximumAllowedBorrowings(userId);
        return ResponseEntity.ok(hasReachedLimit);
    }
    
    /**
     * Get borrowing statistics for a date range (admin only)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public ResponseEntity<Long> getBorrowingStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        long count = borrowingService.countBorrowingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(count);
    }
}

