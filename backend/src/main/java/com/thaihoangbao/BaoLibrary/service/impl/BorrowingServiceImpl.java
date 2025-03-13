package com.thaihoangbao.BaoLibrary.service.impl;

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

import com.thaihoangbao.BaoLibrary.dto.BorrowingDTO;
import com.thaihoangbao.BaoLibrary.dto.BorrowingDTO.BorrowingDetailDTO;
import com.thaihoangbao.BaoLibrary.dto.BorrowingResponseDTO;
import com.thaihoangbao.BaoLibrary.dto.BorrowingResponseDTO.BorrowingDetailResponseDTO;
import com.thaihoangbao.BaoLibrary.dto.UserBorrowingsDTO;
import com.thaihoangbao.BaoLibrary.dto.UserBorrowingsDTO.LoanDTO;
import com.thaihoangbao.BaoLibrary.dto.UserBorrowingsDTO.LoanDetailDTO;
import com.thaihoangbao.BaoLibrary.entity.Book;
import com.thaihoangbao.BaoLibrary.entity.Borrowing;
import com.thaihoangbao.BaoLibrary.entity.Branch;
import com.thaihoangbao.BaoLibrary.entity.LoanDetail;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.repository.BookRepository;
import com.thaihoangbao.BaoLibrary.repository.BorrowingRepository;
import com.thaihoangbao.BaoLibrary.repository.BranchRepository;
import com.thaihoangbao.BaoLibrary.repository.InventoryRepository;
import com.thaihoangbao.BaoLibrary.repository.LoanDetailRepository;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import com.thaihoangbao.BaoLibrary.service.BorrowingService;
import com.thaihoangbao.BaoLibrary.service.NotificationService;

@Service
public class BorrowingServiceImpl implements BorrowingService {

    @Autowired
    private BorrowingRepository borrowingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private BranchRepository branchRepository;
    
    @Autowired
    private LoanDetailRepository loanDetailRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Value("${library.borrowing.max-books:5}")
    private int maxBooksAllowed;
    
    @Override
    @Transactional
    public BorrowingResponseDTO createBorrowing(BorrowingDTO borrowingDTO) {
        // Validate user
        User user = userRepository.findById(borrowingDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + borrowingDTO.getUserId()));
        
        // Validate branch
        Branch branch = branchRepository.findById(borrowingDTO.getBranchId())
            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + borrowingDTO.getBranchId()));
        
        // Check if user has reached maximum allowed borrowings
        if (hasUserReachedMaximumAllowedBorrowings(user.getUserId())) {
            throw new RuntimeException("User has reached the maximum allowed number of borrowed books.");
        }
        
        // Create Borrowing entity
        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setBranch(branch);
        borrowing.setBorrowDate(new Date());
        
        // Save the main borrowing record
        borrowing = borrowingRepository.save(borrowing);
        
        // Process each book in the borrowing request
        List<BorrowingDetailResponseDTO> detailResponseDTOs = new ArrayList<>();
        
        for (BorrowingDetailDTO detailDTO : borrowingDTO.getBorrowingDetails()) {
            // Validate book
            Book book = bookRepository.findById(detailDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + detailDTO.getBookId()));
            
            // Check if book is available in the specified branch
            if (!isBookAvailableForBorrowing(book.getBookId(), branch.getBranchId())) {
                throw new RuntimeException("Book is not available at the specified branch: " + book.getTuaSach());
            }
            
            // Create loan detail
            LoanDetail loanDetail = new LoanDetail();
            loanDetail.setLoan(borrowing);
            loanDetail.setBook(book);
            loanDetail.setDueDate(detailDTO.getDueDate());
            
            // Save loan detail
            loanDetail = loanDetailRepository.save(loanDetail);
            
            // Update inventory (decrease available copies)
            int updated = inventoryRepository.decreaseAvailableCopies(book.getBookId(), branch.getBranchId(), 1);
            if (updated == 0) {
                throw new RuntimeException("Failed to update inventory for book: " + book.getTuaSach());
            }
            
            // Add to response
            BorrowingDetailResponseDTO detailResponseDTO = new BorrowingDetailResponseDTO(
                loanDetail.getLoanDetailId(),
                book.getBookId(),
                book.getTuaSach(),
                loanDetail.getDueDate(),
                null // Not yet returned
            );
            
            detailResponseDTOs.add(detailResponseDTO);
        }
        
        // Send notification
        notificationService.sendBorrowingConfirmation(user.getUserId(), borrowing.getBorrowId());
        
        // Build and return response
        return new BorrowingResponseDTO(
            borrowing.getBorrowId(),
            user.getUserId(),
            user.getHoTen(),
            branch.getBranchId(),
            branch.getTenChiNhanh(),
            borrowing.getBorrowDate(),
            detailResponseDTOs
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowingResponseDTO getBorrowingById(Integer borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
            .orElseThrow(() -> new ResourceNotFoundException("Borrowing not found with ID: " + borrowingId));
        
        List<BorrowingDetailResponseDTO> detailResponseDTOs = borrowing.getLoanDetails().stream()
            .map(detail -> new BorrowingDetailResponseDTO(
                detail.getLoanDetailId(),
                detail.getBook().getBookId(),
                detail.getBook().getTuaSach(),
                detail.getDueDate(),
                detail.getReturnDate()
            ))
            .collect(Collectors.toList());
        
        return new BorrowingResponseDTO(
            borrowing.getBorrowId(),
            borrowing.getUser().getUserId(),
            borrowing.getUser().getHoTen(),
            borrowing.getBranch().getBranchId(),
            borrowing.getBranch().getTenChiNhanh(),
            borrowing.getBorrowDate(),
            detailResponseDTOs
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserBorrowingsDTO getActiveBorrowingsForUser(Integer userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Get active borrowings for the user
        List<Borrowing> activeBorrowings = borrowingRepository.findActiveBorrowingsByUserId(userId);
        
        // Convert to DTOs
        List<LoanDTO> loanDTOs = new ArrayList<>();
        Date currentDate = new Date();
        
        for (Borrowing borrowing : activeBorrowings) {
            List<LoanDetailDTO> loanDetailDTOs = new ArrayList<>();
            
            for (LoanDetail detail : borrowing.getLoanDetails()) {
                // Skip returned books
                if (detail.getReturnDate() != null) {
                    continue;
                }
                
                boolean isOverdue = detail.getDueDate().before(currentDate);
                long daysRemaining = 0;
                
                if (detail.getDueDate() != null) {
                    // Calculate days remaining or days overdue
                    LocalDate dueLocalDate = detail.getDueDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                    LocalDate currentLocalDate = currentDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                    
                    daysRemaining = ChronoUnit.DAYS.between(currentLocalDate, dueLocalDate);
                }
                
                LoanDetailDTO detailDTO = new LoanDetailDTO(
                    detail.getLoanDetailId(),
                    detail.getBook().getBookId(),
                    detail.getBook().getTuaSach(),
                    detail.getBook().getHinhAnhSach(),
                    detail.getDueDate(),
                    isOverdue,
                    daysRemaining
                );
                
                loanDetailDTOs.add(detailDTO);
            }
            
            // Only add the loan if it has active detail items
            if (!loanDetailDTOs.isEmpty()) {
                LoanDTO loanDTO = new LoanDTO(
                    borrowing.getBorrowId(),
                    borrowing.getBorrowDate(),
                    borrowing.getBranch().getBranchId(),
                    borrowing.getBranch().getTenChiNhanh(),
                    loanDetailDTOs
                );
                
                loanDTOs.add(loanDTO);
            }
        }
        
        return new UserBorrowingsDTO(
            user.getUserId(),
            user.getHoTen(),
            loanDTOs
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowingResponseDTO> getAllBorrowingsForUser(Integer userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Get all borrowings for the user
        List<Borrowing> borrowings = borrowingRepository.findByUserUserId(userId);
        
        // Convert to DTOs
        return borrowings.stream().map(borrowing -> {
            List<BorrowingDetailResponseDTO> detailResponseDTOs = borrowing.getLoanDetails().stream()
                .map(detail -> new BorrowingDetailResponseDTO(
                    detail.getLoanDetailId(),
                    detail.getBook().getBookId(),
                    detail.getBook().getTuaSach(),
                    detail.getDueDate(),
                    detail.getReturnDate()
                ))
                .collect(Collectors.toList());
            
            return new BorrowingResponseDTO(
                borrowing.getBorrowId(),
                borrowing.getUser().getUserId(),
                borrowing.getUser().getHoTen(),
                borrowing.getBranch().getBranchId(),
                borrowing.getBranch().getTenChiNhanh(),
                borrowing.getBorrowDate(),
                detailResponseDTOs
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countBorrowingsByDateRange(Date startDate, Date endDate) {
        return borrowingRepository.findByBorrowDateBetween(startDate, endDate).size();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReachedMaximumAllowedBorrowings(Integer userId) {
        // Count how many books the user currently has borrowed
        long currentlyBorrowedCount = loanDetailRepository.countCurrentlyBorrowedBooksByUser(userId);
        
        // Check if the count exceeds the maximum allowed
        return currentlyBorrowedCount >= maxBooksAllowed;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookAvailableForBorrowing(Integer bookId, Integer branchId) {
        return inventoryRepository.isBookAvailableAtBranch(bookId, branchId);
    }
}
