package com.thaihoangbao.BaoLibrary.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import com.thaihoangbao.BaoLibrary.entity.Inventory;
import com.thaihoangbao.BaoLibrary.entity.LoanDetail;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.exception.BadRequestException;
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

    private static final int MAX_BOOKS_PER_USER = 5;
    // Giá thuê sách mỗi ngày
    private static final BigDecimal RENTAL_FEE_PER_DAY = new BigDecimal("10000"); // 10,000 VND per day
    
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
        // Kiểm tra số lượng sách đã mượn của người dùng
        if (hasUserReachedMaximumAllowedBorrowings(borrowingDTO.getUserId())) {
            throw new BadRequestException("Người dùng đã đạt giới hạn số sách được mượn (" + MAX_BOOKS_PER_USER + ")");
        }
        
        // Tìm thông tin người dùng
        User user = userRepository.findById(borrowingDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + borrowingDTO.getUserId()));
        
        // Tìm thông tin chi nhánh
        Branch branch = branchRepository.findById(borrowingDTO.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi nhánh với ID: " + borrowingDTO.getBranchId()));
        
        // Tạo phiếu mượn
        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setBranch(branch);
        borrowing.setBorrowDate(new Date());
        
        // Lưu phiếu mượn để lấy ID
        borrowing = borrowingRepository.save(borrowing);
        
        // Tạo chi tiết phiếu mượn và cập nhật inventory
        List<LoanDetail> loanDetails = new ArrayList<>();
        BigDecimal totalRentalFee = BigDecimal.ZERO;
        
        for (BorrowingDetailDTO detailDTO : borrowingDTO.getBorrowingDetails()) {
            // Kiểm tra sách có tồn tại không
            Book book = bookRepository.findById(detailDTO.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + detailDTO.getBookId()));
            
            // Kiểm tra số lượng sách trong kho
            Inventory inventory = inventoryRepository.findByBookIdAndBranchId(detailDTO.getBookId(), borrowingDTO.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin kho cho sách " + book.getTuaSach()));
            
            if (inventory.getQuantity() <= 0) {
                throw new BadRequestException("Sách " + book.getTuaSach() + " đã hết");
            }
            
            // Giảm số lượng sách trong kho
            inventory.setQuantity(inventory.getQuantity() - 1);
            inventoryRepository.save(inventory);
            
            // Tính ngày hết hạn và phí thuê
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            
            // Tính ngày dự kiến trả sách
            Calendar expectedReturnCal = Calendar.getInstance();
            expectedReturnCal.setTime(new Date());
            expectedReturnCal.add(Calendar.DAY_OF_MONTH, detailDTO.getRentalDays());
            Date expectedReturnDate = expectedReturnCal.getTime();
            
            // Mặc định ngày đến hạn = ngày dự kiến trả
            Date dueDate = expectedReturnDate;
            
            // Tính phí thuê sách
            BigDecimal rentalFee = RENTAL_FEE_PER_DAY.multiply(new BigDecimal(detailDTO.getRentalDays()));
            totalRentalFee = totalRentalFee.add(rentalFee);
            
            // Tạo chi tiết phiếu mượn
            LoanDetail loanDetail = new LoanDetail();
            loanDetail.setLoan(borrowing);
            loanDetail.setBook(book);
            loanDetail.setDueDate(dueDate);
            loanDetail.setRentalDays(detailDTO.getRentalDays());
            loanDetail.setRentalFee(rentalFee);
            loanDetail.setExpectedReturnDate(expectedReturnDate);
            
            loanDetails.add(loanDetail);
            loanDetailRepository.save(loanDetail);
        }
        
        // Gửi thông báo cho người dùng
        String notificationMessage = "Bạn đã mượn " + loanDetails.size() + " sách thành công. Tổng phí thuê: " + totalRentalFee + " VND.";
        notificationService.sendNotification(user.getUserId(), "Mượn sách thành công", notificationMessage);
        
        // Trả về thông tin phiếu mượn
        return convertToBorrowingResponseDTO(borrowing, loanDetails, totalRentalFee);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowingResponseDTO getBorrowingById(Integer borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn với ID: " + borrowingId));
        
        List<LoanDetail> loanDetails = loanDetailRepository.findByLoan(borrowing);
        
        // Tính tổng phí thuê
        BigDecimal totalRentalFee = loanDetails.stream()
                .map(LoanDetail::getRentalFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return convertToBorrowingResponseDTO(borrowing, loanDetails, totalRentalFee);
    }

    @Override
    @Transactional(readOnly = true)
    public UserBorrowingsDTO getActiveBorrowingsForUser(Integer userId) {
        // Kiểm tra người dùng có tồn tại không
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Lấy danh sách phiếu mượn của người dùng
        List<Borrowing> borrowings = borrowingRepository.findByUser(user);
        
        // Lọc các phiếu mượn còn sách chưa trả
        List<BorrowingResponseDTO> activeBorrowings = new ArrayList<>();
        
        for (Borrowing borrowing : borrowings) {
            List<LoanDetail> loanDetails = loanDetailRepository.findByLoan(borrowing);
            
            // Kiểm tra xem có chi tiết nào chưa trả sách không
            boolean hasUnreturnedBooks = loanDetails.stream()
                    .anyMatch(detail -> detail.getReturnDate() == null);
            
            if (hasUnreturnedBooks) {
                // Tính tổng phí thuê
                BigDecimal totalRentalFee = loanDetails.stream()
                        .map(LoanDetail::getRentalFee)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                activeBorrowings.add(convertToBorrowingResponseDTO(borrowing, loanDetails, totalRentalFee));
            }
        }
        
        UserBorrowingsDTO result = new UserBorrowingsDTO();
        result.setUserId(userId);
        result.setUserName(user.getHoTen());
        result.setBorrowings(activeBorrowings);
        result.setTotalActiveBorrowings(activeBorrowings.size());
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowingResponseDTO> getAllBorrowingsForUser(Integer userId) {
        // Kiểm tra người dùng có tồn tại không
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Lấy danh sách phiếu mượn của người dùng
        List<Borrowing> borrowings = borrowingRepository.findByUser(user);
        
        // Chuyển đổi sang DTO
        List<BorrowingResponseDTO> result = new ArrayList<>();
        
        for (Borrowing borrowing : borrowings) {
            List<LoanDetail> loanDetails = loanDetailRepository.findByLoan(borrowing);
            
            // Tính tổng phí thuê
            BigDecimal totalRentalFee = loanDetails.stream()
                    .map(LoanDetail::getRentalFee)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            result.add(convertToBorrowingResponseDTO(borrowing, loanDetails, totalRentalFee));
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public long countBorrowingsByDateRange(Date startDate, Date endDate) {
        return borrowingRepository.countByBorrowDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReachedMaximumAllowedBorrowings(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Đếm số sách hiện đang mượn (chưa trả)
        int currentBorrowedBooks = loanDetailRepository.countByLoanUserAndReturnDateIsNull(user);
        
        return currentBorrowedBooks >= MAX_BOOKS_PER_USER;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookAvailableForBorrowing(Integer bookId, Integer branchId) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByBookIdAndBranchId(bookId, branchId);
        
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            return inventory.getQuantity() > 0;
        }
        
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateRentalFee(Integer rentalDays) {
        if (rentalDays <= 0) {
            return BigDecimal.ZERO;
        }
        
        return RENTAL_FEE_PER_DAY.multiply(new BigDecimal(rentalDays));
    }
    
    // Helper methods
    private BorrowingResponseDTO convertToBorrowingResponseDTO(Borrowing borrowing, List<LoanDetail> loanDetails, BigDecimal totalRentalFee) {
        BorrowingResponseDTO responseDTO = new BorrowingResponseDTO();
        responseDTO.setLoanId(borrowing.getBorrowId());
        responseDTO.setUserId(borrowing.getUser().getUserId());
        responseDTO.setUserName(borrowing.getUser().getHoTen());
        responseDTO.setBranchId(borrowing.getBranch().getBranchId());
        responseDTO.setBranchName(borrowing.getBranch().getBranchName());
        responseDTO.setBorrowDate(borrowing.getBorrowDate());
        responseDTO.setTotalRentalFee(totalRentalFee);
        
        List<BorrowingDetailResponseDTO> detailDTOs = loanDetails.stream()
                .map(this::convertToDetailResponseDTO)
                .collect(Collectors.toList());
        
        responseDTO.setBorrowingDetails(detailDTOs);
        
        return responseDTO;
    }
    
    private BorrowingDetailResponseDTO convertToDetailResponseDTO(LoanDetail loanDetail) {
        BorrowingDetailResponseDTO detailDTO = new BorrowingDetailResponseDTO();
        detailDTO.setLoanDetailId(loanDetail.getLoanDetailId());
        detailDTO.setBookId(loanDetail.getBook().getBookId());
        detailDTO.setBookTitle(loanDetail.getBook().getTuaSach());
        detailDTO.setRentalDays(loanDetail.getRentalDays());
        detailDTO.setRentalFee(loanDetail.getRentalFee());
        detailDTO.setExpectedReturnDate(loanDetail.getExpectedReturnDate());
        detailDTO.setDueDate(loanDetail.getDueDate());
        detailDTO.setReturnDate(loanDetail.getReturnDate());
        
        return detailDTO;
    }
}
