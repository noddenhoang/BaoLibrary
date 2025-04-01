package com.thaihoangbao.BaoLibrary.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.dto.ViolationDTO;
import com.thaihoangbao.BaoLibrary.security.SecurityHelper;
import com.thaihoangbao.BaoLibrary.service.UserService;
import com.thaihoangbao.BaoLibrary.service.ViolationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    @Autowired
    private ViolationService violationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SecurityHelper securityHelper;
    
    // Tạo vi phạm mới (chỉ admin hoặc manager)
    @PostMapping
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    public ResponseEntity<ViolationDTO> createViolation(@Valid @RequestBody ViolationDTO violationDTO) {
        return new ResponseEntity<>(violationService.createViolation(violationDTO), HttpStatus.CREATED);
    }
    
    // Lấy thông tin vi phạm theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager') or authentication.principal.username == @userService.getUserById(@violationService.getViolationById(#id).userId).taiKhoan")
    public ResponseEntity<ViolationDTO> getViolationById(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(violationService.getViolationById(id));
    }
    
    // Lấy danh sách tất cả vi phạm (chỉ admin hoặc manager)
    @GetMapping
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    public PagedResponse<ViolationDTO> getAllViolations(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "violationId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {
        return violationService.getAllViolations(pageNo, pageSize, sortBy, sortDir);
    }
    
    // Lấy danh sách vi phạm của một người dùng
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager') or authentication.principal.username == @userService.getUserById(#userId).taiKhoan")
    public PagedResponse<ViolationDTO> getViolationsByUser(
            @PathVariable(value = "userId") Integer userId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return violationService.getViolationsByUser(userId, pageNo, pageSize);
    }
    
    // Lấy danh sách các vi phạm chưa thanh toán của người dùng
    @GetMapping("/user/{userId}/unpaid")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager') or authentication.principal.username == @userService.getUserById(#userId).taiKhoan")
    public ResponseEntity<List<ViolationDTO>> getUnpaidViolations(@PathVariable(value = "userId") Integer userId) {
        return ResponseEntity.ok(violationService.getUnpaidViolations(userId));
    }
    
    // Lấy tổng số tiền phạt chưa thanh toán của người dùng
    @GetMapping("/user/{userId}/total-unpaid")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager') or authentication.principal.username == @userService.getUserById(#userId).taiKhoan")
    public ResponseEntity<BigDecimal> getTotalUnpaidFines(@PathVariable(value = "userId") Integer userId) {
        return ResponseEntity.ok(violationService.getTotalUnpaidFines(userId));
    }
    
    // Đánh dấu vi phạm đã thanh toán
    @PatchMapping("/{id}/mark-paid")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    public ResponseEntity<Void> markViolationAsPaid(@PathVariable(value = "id") Integer id) {
        violationService.markViolationAsPaid(id);
        return ResponseEntity.ok().build();
    }
}
