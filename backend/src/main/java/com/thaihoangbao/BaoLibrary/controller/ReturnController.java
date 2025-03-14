package com.thaihoangbao.BaoLibrary.controller;

import java.math.BigDecimal;
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

import com.thaihoangbao.BaoLibrary.dto.ReturnDTO;
import com.thaihoangbao.BaoLibrary.dto.ReturnResponseDTO;
import com.thaihoangbao.BaoLibrary.service.ReturnService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    @Autowired
    private ReturnService returnService;
    
    /**
     * Process book returns
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isCurrentUser(#returnDTO.userId)")
    public ResponseEntity<ReturnResponseDTO> processBookReturns(@Valid @RequestBody ReturnDTO returnDTO) {
        ReturnResponseDTO response = returnService.processBookReturns(returnDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get a return by ID
     */
    @GetMapping("/{returnId}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isUserOfReturn(#returnId)")
    public ResponseEntity<ReturnResponseDTO> getReturnById(@PathVariable Integer returnId) {
        ReturnResponseDTO response = returnService.getReturnById(returnId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get returns by date range (admin only)
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public ResponseEntity<List<ReturnResponseDTO>> getReturnsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<ReturnResponseDTO> response = returnService.getReturnsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Calculate late fee for a loan detail
     */
    @GetMapping("/calculate-late-fee/{loanDetailId}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isUserOfLoanDetail(#loanDetailId)")
    public ResponseEntity<BigDecimal> calculateLateFee(@PathVariable Integer loanDetailId) {
        BigDecimal lateFee = returnService.calculateLateFee(loanDetailId);
        return ResponseEntity.ok(lateFee);
    }
    
    /**
     * Send due date reminders (admin only)
     */
    @PostMapping("/send-reminders")
    @PreAuthorize("hasAnyAuthority('manager', 'admin')")
    public ResponseEntity<Void> sendDueDateReminders() {
        returnService.sendDueDateReminders();
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get on-time return rate for a user
     */
    @GetMapping("/on-time-rate/{userId}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isCurrentUser(#userId)")
    public ResponseEntity<Double> getOnTimeReturnRate(@PathVariable Integer userId) {
        double rate = returnService.getOnTimeReturnRate(userId);
        return ResponseEntity.ok(rate);
    }
    
    /**
     * Check if all books in a loan have been returned
     */
    @GetMapping("/check-all-returned/{loanId}")
    @PreAuthorize("hasAnyAuthority('manager', 'admin') or @securityHelper.isUserOfBorrowing(#loanId)")
    public ResponseEntity<Boolean> areAllBooksInLoanReturned(@PathVariable Integer loanId) {
        boolean allReturned = returnService.areAllBooksInLoanReturned(loanId);
        return ResponseEntity.ok(allReturned);
    }
}

