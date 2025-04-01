package com.thaihoangbao.BaoLibrary.controller;

import com.thaihoangbao.BaoLibrary.dto.BookResponseDto;
import com.thaihoangbao.BaoLibrary.service.BookService;
import com.thaihoangbao.BaoLibrary.service.BorrowingService;
import com.thaihoangbao.BaoLibrary.service.ReturnService;
import com.thaihoangbao.BaoLibrary.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
public class StatisticsController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private ReturnService returnService;
    
    @Autowired
    private ViolationService violationService;
    
    /**
     * Thống kê tổng quan thư viện
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverviewStatistics() {
        Date today = new Date();
        Date startOfMonth = getStartOfMonth(today);
        
        Map<String, Object> response = new HashMap<>();
        
        // Số lượng mượn trong tháng
        long borrowingsThisMonth = borrowingService.countBorrowingsByDateRange(startOfMonth, today);
        response.put("borrowingsThisMonth", borrowingsThisMonth);
        
        // Số lượng trả trong tháng
        long returnsThisMonth = returnService.countReturnsByDateRange(startOfMonth, today);
        response.put("returnsThisMonth", returnsThisMonth);
        
        // Tỷ lệ trả đúng hạn
        double onTimeReturnRate = returnService.getOnTimeReturnRateOverall();
        response.put("onTimeReturnRate", onTimeReturnRate);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Thống kê mượn/trả trong một khoảng thời gian
     */
    @GetMapping("/borrowing-return")
    public ResponseEntity<Map<String, Object>> getBorrowingReturnStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Số lượng mượn trong khoảng thời gian
        long borrowingsCount = borrowingService.countBorrowingsByDateRange(startDate, endDate);
        response.put("borrowingsCount", borrowingsCount);
        
        // Số lượng trả trong khoảng thời gian
        long returnsCount = returnService.countReturnsByDateRange(startDate, endDate);
        response.put("returnsCount", returnsCount);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Thống kê vi phạm
     */
    @GetMapping("/violations")
    public ResponseEntity<Map<String, Object>> getViolationStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Tổng số vi phạm trong khoảng thời gian
        long violationsCount = returnService.countViolationsByDateRange(startDate, endDate);
        response.put("violationsCount", violationsCount);
        
        // Tổng số tiền phạt đã thu trong khoảng thời gian
        BigDecimal totalFines = returnService.getTotalFinesByDateRange(startDate, endDate);
        response.put("totalFines", totalFines);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Helper method to get the first day of the month
     */
    private Date getStartOfMonth(Date date) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
} 