package com.thaihoangbao.BaoLibrary.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.dto.ViolationDTO;
import com.thaihoangbao.BaoLibrary.entity.LoanDetail;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.entity.Violation;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.repository.LoanDetailRepository;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import com.thaihoangbao.BaoLibrary.repository.ViolationRepository;
import com.thaihoangbao.BaoLibrary.service.NotificationService;
import com.thaihoangbao.BaoLibrary.service.ViolationService;

@Service
public class ViolationServiceImpl implements ViolationService {

    @Autowired
    private ViolationRepository violationRepository;
    
    @Autowired
    private LoanDetailRepository loanDetailRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Value("${library.fine.rate:5000}")
    private BigDecimal fineRatePerDay; // Mức phí phạt mặc định là 5,000 VND/ngày

    @Override
    @Transactional
    public ViolationDTO createViolation(ViolationDTO violationDTO) {
        // Kiểm tra loan detail
        LoanDetail loanDetail = loanDetailRepository.findById(violationDTO.getLoanDetailId())
                .orElseThrow(() -> new ResourceNotFoundException("LoanDetail not found with id: " + violationDTO.getLoanDetailId()));
        
        // Tạo vi phạm mới
        Violation violation = new Violation();
        violation.setLoanDetail(loanDetail);
        violation.setViolationType(violationDTO.getViolationType());
        violation.setViolationDate(violationDTO.getViolationDate() != null ? violationDTO.getViolationDate() : new Date());
        violation.setFineAmount(violationDTO.getFineAmount() != null ? violationDTO.getFineAmount() : calculateLateFee(loanDetail));
        violation.setStatus("pending");
        
        Violation savedViolation = violationRepository.save(violation);
        
        // Gửi thông báo
        Integer userId = loanDetail.getLoan().getUser().getUserId();
        notificationService.sendLateFeeNotification(userId, savedViolation.getViolationId());
        
        return convertToDTO(savedViolation);
    }

    @Override
    @Transactional(readOnly = true)
    public ViolationDTO getViolationById(Integer id) {
        Violation violation = violationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Violation not found with id: " + id));
        return convertToDTO(violation);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ViolationDTO> getAllViolations(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Violation> violationsPage = violationRepository.findAll(pageable);
        
        List<Violation> violations = violationsPage.getContent();
        List<ViolationDTO> content = violations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                content,
                violationsPage.getNumber(),
                violationsPage.getSize(),
                violationsPage.getTotalElements(),
                violationsPage.getTotalPages(),
                violationsPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ViolationDTO> getViolationsByUser(Integer userId, int pageNo, int pageSize) {
        // Kiểm tra người dùng tồn tại
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<Violation> violations = violationRepository.findPendingViolationsByUserId(userId);
        
        List<ViolationDTO> content = violations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                content,
                pageNo,
                pageSize,
                (long) violations.size(),
                (int) Math.ceil((double) violations.size() / pageSize),
                pageNo >= (int) Math.ceil((double) violations.size() / pageSize) - 1
        );
    }

    @Override
    @Transactional
    public BigDecimal calculateLateFee(LoanDetail loanDetail) {
        if (loanDetail.getDueDate() == null || loanDetail.getReturnDate() == null) {
            return BigDecimal.ZERO;
        }
        
        // Nếu trả đúng hạn hoặc sớm hơn
        if (!loanDetail.getReturnDate().after(loanDetail.getDueDate())) {
            return BigDecimal.ZERO;
        }
        
        // Tính số ngày trễ
        long diffInMillies = Math.abs(loanDetail.getReturnDate().getTime() - loanDetail.getDueDate().getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        
        // Tính phí phạt dựa trên số ngày trễ
        return fineRatePerDay.multiply(BigDecimal.valueOf(diffInDays));
    }

    @Override
    @Transactional
    public void markViolationAsPaid(Integer id) {
        Violation violation = violationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Violation not found with id: " + id));
        
        violation.setStatus("paid");
        violationRepository.save(violation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViolationDTO> getUnpaidViolations(Integer userId) {
        List<Violation> violations = violationRepository.findPendingViolationsByUserId(userId);
        return violations.stream()
                .filter(v -> "pending".equals(v.getStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalUnpaidFines(Integer userId) {
        return violationRepository.findTotalUnpaidFinesByUserId(userId);
    }
    
    // Helper method để chuyển đổi Entity thành DTO
    private ViolationDTO convertToDTO(Violation violation) {
        ViolationDTO violationDTO = new ViolationDTO();
        violationDTO.setViolationId(violation.getViolationId());
        violationDTO.setLoanDetailId(violation.getLoanDetail().getLoanDetailId());
        violationDTO.setViolationType(violation.getViolationType());
        // Nếu cần thông tin description, bạn có thể tạo một mô tả từ loại vi phạm
        violationDTO.setDescription("Vi phạm: " + violation.getViolationType());
        violationDTO.setViolationDate(violation.getViolationDate());
        violationDTO.setFineAmount(violation.getFineAmount());
        // Xác định isPaid dựa trên trạng thái
        violationDTO.setIsPaid("paid".equals(violation.getStatus()));
        
        // Set user ID và book title nếu có
        User user = violation.getLoanDetail().getLoan().getUser();
        violationDTO.setUserId(user.getUserId());
        
        String bookTitle = violation.getLoanDetail().getBook().getTuaSach();
        violationDTO.setBookTitle(bookTitle);
        
        return violationDTO;
    }
} 