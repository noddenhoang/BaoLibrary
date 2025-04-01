package com.thaihoangbao.BaoLibrary.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.thaihoangbao.BaoLibrary.dto.ReturnDTO;
import com.thaihoangbao.BaoLibrary.dto.ReturnResponseDTO;

public interface ReturnService {
    
    /**
     * Process book returns, update inventory, and calculate late fees if applicable
     */
    ReturnResponseDTO processBookReturns(ReturnDTO returnDTO);
    
    /**
     * Get details of a specific return by ID
     */
    ReturnResponseDTO getReturnById(Integer returnId);
    
    /**
     * Get returns processed in a date range
     */
    List<ReturnResponseDTO> getReturnsByDateRange(Date startDate, Date endDate);
    
    /**
     * Calculate late fee for an overdue loan detail
     */
    BigDecimal calculateLateFee(Integer loanDetailId);
    
    /**
     * Calculate all due dates approaching for a user and send notifications
     */
    void sendDueDateReminders();
    
    /**
     * Get on-time return rate statistics for a user
     */
    double getOnTimeReturnRate(Integer userId);
    
    /**
     * Verify if all books in a loan have been returned
     */
    boolean areAllBooksInLoanReturned(Integer loanId);
    
    /**
     * Count returns processed in a date range
     */
    long countReturnsByDateRange(Date startDate, Date endDate);
    
    /**
     * Get overall on-time return rate
     */
    double getOnTimeReturnRateOverall();
    
    /**
     * Count violations created in a date range
     */
    long countViolationsByDateRange(Date startDate, Date endDate);
    
    /**
     * Get total fines collected in a date range
     */
    BigDecimal getTotalFinesByDateRange(Date startDate, Date endDate);
}
