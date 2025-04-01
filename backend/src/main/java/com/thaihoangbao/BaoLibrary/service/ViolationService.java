package com.thaihoangbao.BaoLibrary.service;

import java.math.BigDecimal;
import java.util.List;

import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.dto.ViolationDTO;
import com.thaihoangbao.BaoLibrary.entity.LoanDetail;

public interface ViolationService {
    ViolationDTO createViolation(ViolationDTO violationDTO);
    
    ViolationDTO getViolationById(Integer id);
    
    PagedResponse<ViolationDTO> getAllViolations(int pageNo, int pageSize, String sortBy, String sortDir);
    
    PagedResponse<ViolationDTO> getViolationsByUser(Integer userId, int pageNo, int pageSize);
    
    BigDecimal calculateLateFee(LoanDetail loanDetail);
    
    void markViolationAsPaid(Integer id);
    
    List<ViolationDTO> getUnpaidViolations(Integer userId);
    
    BigDecimal getTotalUnpaidFines(Integer userId);
}
