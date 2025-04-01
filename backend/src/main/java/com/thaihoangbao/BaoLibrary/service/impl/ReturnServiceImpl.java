package com.thaihoangbao.BaoLibrary.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoangbao.BaoLibrary.dto.ReturnDTO;
import com.thaihoangbao.BaoLibrary.dto.ReturnDTO.ReturnDetailDTO;
import com.thaihoangbao.BaoLibrary.dto.ReturnResponseDTO;
import com.thaihoangbao.BaoLibrary.dto.ReturnResponseDTO.ReturnDetailResponseDTO;
import com.thaihoangbao.BaoLibrary.entity.LoanDetail;
import com.thaihoangbao.BaoLibrary.entity.Return;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.entity.Violation;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.repository.InventoryRepository;
import com.thaihoangbao.BaoLibrary.repository.LoanDetailRepository;
import com.thaihoangbao.BaoLibrary.repository.ReturnRepository;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import com.thaihoangbao.BaoLibrary.repository.ViolationRepository;
import com.thaihoangbao.BaoLibrary.service.NotificationService;
import com.thaihoangbao.BaoLibrary.service.ReturnService;

@Service
public class ReturnServiceImpl implements ReturnService {

    @Autowired
    private ReturnRepository returnRepository;
    
    @Autowired
    private LoanDetailRepository loanDetailRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private ViolationRepository violationRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Value("${library.fees.late-fee-per-day:10000}")
    private BigDecimal lateFeePerDay; // Default to 10,000 VND per day
    
    @Value("${library.fees.max-late-fee:200000}")
    private BigDecimal maxLateFee; // Maximum late fee cap (200,000 VND)
    
    @Override
    @Transactional
    public ReturnResponseDTO processBookReturns(ReturnDTO returnDTO) {
        // Validate user
        User user = userRepository.findById(returnDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + returnDTO.getUserId()));
        
        Date returnDate = new Date();
        BigDecimal totalLateFees = BigDecimal.ZERO;
        List<ReturnDetailResponseDTO> returnDetailResponseDTOs = new ArrayList<>();
        
        for (ReturnDetailDTO detailDTO : returnDTO.getReturnDetails()) {
            // Get loan detail
            LoanDetail loanDetail = loanDetailRepository.findById(detailDTO.getLoanDetailId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan detail not found with ID: " + detailDTO.getLoanDetailId()));
            
            // Check if book is already returned
            if (loanDetail.getReturnDate() != null) {
                throw new RuntimeException("Book already returned: " + loanDetail.getBook().getTuaSach());
            }
            
            // Check if loan belongs to the requesting user
            if (!loanDetail.getLoan().getUser().getUserId().equals(user.getUserId())) {
                throw new RuntimeException("Loan detail does not belong to the requesting user");
            }
            
            // Calculate late fee if applicable
            BigDecimal lateFee = BigDecimal.ZERO;
            boolean isLate = false;
            
            if (returnDate.after(loanDetail.getDueDate())) {
                isLate = true;
                lateFee = calculateLateFee(loanDetail.getLoanDetailId());
                totalLateFees = totalLateFees.add(lateFee);
                
                // Create violation record for late return
                if (lateFee.compareTo(BigDecimal.ZERO) > 0) {
                    Violation violation = new Violation();
                    violation.setLoanDetail(loanDetail);
                    violation.setViolationType("late_return");
                    violation.setFineAmount(lateFee);
                    violation.setViolationDate(returnDate);
                    violation.setStatus("pending");
                    
                    violationRepository.save(violation);
                    
                    // Send late fee notification
                    notificationService.sendLateFeeNotification(user.getUserId(), violation.getViolationId());
                }
            }
            
            // Update loan detail with return date
            loanDetail.setReturnDate(returnDate);
            loanDetail = loanDetailRepository.save(loanDetail);
            
            // Create return record
            Return returnRecord = new Return();
            returnRecord.setLoanDetail(loanDetail);
            returnRecord.setReturnDate(returnDate);
            returnRecord.setLateFee(lateFee);
            returnRecord.setBookCondition(detailDTO.getBookCondition());
            returnRecord.setComments(detailDTO.getComments());
            
            returnRecord = returnRepository.save(returnRecord);
            
            // Update inventory (increase available copies)
            inventoryRepository.increaseAvailableCopies(
                loanDetail.getBook().getBookId(),
                loanDetail.getLoan().getBranch().getBranchId(),
                1
            );
            
            // Add to response
            ReturnDetailResponseDTO detailResponseDTO = new ReturnDetailResponseDTO(
                loanDetail.getLoanDetailId(),
                loanDetail.getBook().getBookId(),
                loanDetail.getBook().getTuaSach(),
                loanDetail.getDueDate(),
                returnDate,
                isLate,
                lateFee,
                detailDTO.getBookCondition(),
                detailDTO.getComments()
            );
            
            returnDetailResponseDTOs.add(detailResponseDTO);
        }
        
        // Send return confirmation notification
        notificationService.sendReturnConfirmation(user.getUserId(), 0); // Using 0 as a placeholder for return ID
        
        // Build and return response
        return new ReturnResponseDTO(
            user.getUserId(),
            user.getHoTen(),
            returnDate,
            totalLateFees,
            returnDetailResponseDTOs
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnResponseDTO getReturnById(Integer returnId) {
        Return returnRecord = returnRepository.findById(returnId)
            .orElseThrow(() -> new ResourceNotFoundException("Return not found with ID: " + returnId));
        
        LoanDetail loanDetail = returnRecord.getLoanDetail();
        User user = loanDetail.getLoan().getUser();
        
        ReturnDetailResponseDTO detailResponseDTO = new ReturnDetailResponseDTO(
            loanDetail.getLoanDetailId(),
            loanDetail.getBook().getBookId(),
            loanDetail.getBook().getTuaSach(),
            loanDetail.getDueDate(),
            returnRecord.getReturnDate(),
            returnRecord.getLateFee().compareTo(BigDecimal.ZERO) > 0,
            returnRecord.getLateFee(),
            returnRecord.getBookCondition(),
            returnRecord.getComments()
        );
        
        List<ReturnDetailResponseDTO> details = new ArrayList<>();
        details.add(detailResponseDTO);
        
        return new ReturnResponseDTO(
            user.getUserId(),
            user.getHoTen(),
            returnRecord.getReturnDate(),
            returnRecord.getLateFee(),
            details
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnResponseDTO> getReturnsByDateRange(Date startDate, Date endDate) {
        List<Return> returns = returnRepository.findByReturnDateBetween(startDate, endDate);
        
        // Group returns by user and return date
        return returns.stream()
            .collect(Collectors.groupingBy(
                r -> {
                    User user = r.getLoanDetail().getLoan().getUser();
                    return user.getUserId() + "_" + r.getReturnDate().toString();
                }
            ))
            .values()
            .stream()
            .map(userReturns -> {
                User user = userReturns.get(0).getLoanDetail().getLoan().getUser();
                Date returnDate = userReturns.get(0).getReturnDate();
                
                List<ReturnDetailResponseDTO> details = userReturns.stream()
                    .map(r -> {
                        LoanDetail ld = r.getLoanDetail();
                        return new ReturnDetailResponseDTO(
                            ld.getLoanDetailId(),
                            ld.getBook().getBookId(),
                            ld.getBook().getTuaSach(),
                            ld.getDueDate(),
                            r.getReturnDate(),
                            r.getLateFee().compareTo(BigDecimal.ZERO) > 0,
                            r.getLateFee(),
                            r.getBookCondition(),
                            r.getComments()
                        );
                    })
                    .collect(Collectors.toList());
                
                BigDecimal totalLateFees = userReturns.stream()
                    .map(Return::getLateFee)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                return new ReturnResponseDTO(
                    user.getUserId(),
                    user.getHoTen(),
                    returnDate,
                    totalLateFees,
                    details
                );
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateLateFee(Integer loanDetailId) {
        LoanDetail loanDetail = loanDetailRepository.findById(loanDetailId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan detail not found with ID: " + loanDetailId));
        
        Date dueDate = loanDetail.getDueDate();
        Date returnDate = new Date(); // Current date if not yet returned
        
        if (loanDetail.getReturnDate() != null) {
            returnDate = loanDetail.getReturnDate();
        }
        
        if (!returnDate.after(dueDate)) {
            return BigDecimal.ZERO; // Not late
        }
        
        // Calculate days late
        long daysLate = ChronoUnit.DAYS.between(
            dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            returnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        );
        
        // Calculate fee
        BigDecimal fee = lateFeePerDay.multiply(BigDecimal.valueOf(daysLate));
        
        // Cap fee at maximum allowed
        return fee.min(maxLateFee);
    }

    @Override
    @Transactional
    public void sendDueDateReminders() {
        // Get current date
        LocalDate today = LocalDate.now();
        LocalDate reminderThreshold = today.plusDays(2); // Send reminders for books due in 2 days
        
        // Convert to Date
        Date thresholdDate = Date.from(reminderThreshold.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        // Find loan details with due date approaching
        List<LoanDetail> dueSoonDetails = loanDetailRepository.findAll().stream()
            .filter(ld -> ld.getReturnDate() == null) // Not yet returned
            .filter(ld -> {
                LocalDate dueDate = ld.getDueDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
                return ChronoUnit.DAYS.between(today, dueDate) <= 2 && ChronoUnit.DAYS.between(today, dueDate) >= 0;
            })
            .collect(Collectors.toList());
        
        // Send reminders
        for (LoanDetail detail : dueSoonDetails) {
            Integer userId = detail.getLoan().getUser().getUserId();
            notificationService.sendDueDateReminder(userId, detail.getLoanDetailId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public double getOnTimeReturnRate(Integer userId) {
        // Get all returns for the user
        List<Return> userReturns = returnRepository.findAll().stream()
            .filter(r -> r.getLoanDetail().getLoan().getUser().getUserId().equals(userId))
            .collect(Collectors.toList());
        
        if (userReturns.isEmpty()) {
            return 100.0; // No returns yet, default to perfect rate
        }
        
        // Count on-time returns
        long onTimeCount = userReturns.stream()
            .filter(r -> !r.getReturnDate().after(r.getLoanDetail().getDueDate()))
            .count();
        
        // Calculate rate
        return (double) onTimeCount / userReturns.size() * 100.0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areAllBooksInLoanReturned(Integer loanId) {
        List<LoanDetail> loanDetails = loanDetailRepository.findByLoanBorrowId(loanId);
        
        // Check if all books are returned
        return loanDetails.stream().allMatch(detail -> detail.getReturnDate() != null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countReturnsByDateRange(Date startDate, Date endDate) {
        return returnRepository.countByReturnDateBetween(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public double getOnTimeReturnRateOverall() {
        long totalReturns = returnRepository.count();
        if (totalReturns == 0) {
            return 0.0;
        }
        
        // Đếm số lần trả đúng hạn (không có vi phạm)
        long onTimeReturns = returnRepository.countOnTimeReturns();
        
        return (double) onTimeReturns / totalReturns;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countViolationsByDateRange(Date startDate, Date endDate) {
        return violationRepository.countByViolationDateBetween(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalFinesByDateRange(Date startDate, Date endDate) {
        // Lấy tổng số tiền phạt đã thu trong khoảng thời gian
        BigDecimal totalFines = violationRepository.sumPaidFinesByDateRange(startDate, endDate);
        return totalFines != null ? totalFines : BigDecimal.ZERO;
    }
}
