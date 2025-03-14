package com.thaihoangbao.BaoLibrary.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.thaihoangbao.BaoLibrary.entity.Borrowing;
import com.thaihoangbao.BaoLibrary.entity.LoanDetail;
import com.thaihoangbao.BaoLibrary.entity.Return;
import com.thaihoangbao.BaoLibrary.repository.BorrowingRepository;
import com.thaihoangbao.BaoLibrary.repository.LoanDetailRepository;
import com.thaihoangbao.BaoLibrary.repository.ReturnRepository;

/**
 * Helper class for security-related operations in Spring Security expressions
 */
@Component("securityHelper")
public class SecurityHelper {

    @Autowired
    private BorrowingRepository borrowingRepository;
    
    @Autowired
    private LoanDetailRepository loanDetailRepository;
    
    @Autowired
    private ReturnRepository returnRepository;

    /**
     * Get the username of the currently authenticated user
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        
        return null;
    }
    
    /**
     * Get the user ID of the currently authenticated user
     */
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        
        return null;
    }

    /**
     * Check if the current authenticated user matches the given userId
     */
    public boolean isCurrentUser(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // If principal is CustomUserDetails, get the ID and compare
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId().equals(userId);
        }
        
        return false;
    }
    
    /**
     * Check if the current user is the owner of the borrowing
     */
    public boolean isUserOfBorrowing(Integer borrowingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer currentUserId = userDetails.getUserId();
            
            // Get the borrowing and check if the user is the owner
            Optional<Borrowing> borrowingOpt = borrowingRepository.findById(borrowingId);
            if (borrowingOpt.isPresent()) {
                return borrowingOpt.get().getUser().getUserId().equals(currentUserId);
            }
        }
        
        return false;
    }
    
    /**
     * Check if the current user is the owner of the loan detail
     */
    public boolean isUserOfLoanDetail(Integer loanDetailId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer currentUserId = userDetails.getUserId();
            
            // Get the loan detail and check if the user is the owner
            Optional<LoanDetail> loanDetailOpt = loanDetailRepository.findById(loanDetailId);
            if (loanDetailOpt.isPresent()) {
                return loanDetailOpt.get().getLoan().getUser().getUserId().equals(currentUserId);
            }
        }
        
        return false;
    }
    
    /**
     * Check if the current user is the owner of the return
     */
    public boolean isUserOfReturn(Integer returnId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer currentUserId = userDetails.getUserId();
            
            // Get the return and check if the user is the owner
            Optional<Return> returnOpt = returnRepository.findById(returnId);
            if (returnOpt.isPresent()) {
                return returnOpt.get().getLoanDetail().getLoan().getUser().getUserId().equals(currentUserId);
            }
        }
        
        return false;
    }
} 