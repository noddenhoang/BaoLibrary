package com.thaihoangbao.BaoLibrary.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.Book;
import com.thaihoangbao.BaoLibrary.entity.Borrowing;
import com.thaihoangbao.BaoLibrary.entity.LoanDetail;
import com.thaihoangbao.BaoLibrary.entity.User;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Integer> {
    
    // Find all loan details for a loan
    List<LoanDetail> findByLoan(Borrowing loan);
    
    // Find all loan details for a loan ID
    List<LoanDetail> findByLoanBorrowId(Integer loanId);
    
    // Find all loan details for a book
    List<LoanDetail> findByBook(Book book);
    
    // Find all loan details for a book ID
    List<LoanDetail> findByBookBookId(Integer bookId);
    
    // Find all non-returned books
    List<LoanDetail> findByReturnDateIsNull();
    
    // Find all overdue books
    @Query("SELECT ld FROM LoanDetail ld WHERE ld.returnDate IS NULL AND ld.dueDate < :currentDate")
    List<LoanDetail> findOverdueBooks(@Param("currentDate") Date currentDate);
    
    // Check if a book is currently borrowed by a specific user
    @Query("SELECT COUNT(ld) > 0 FROM LoanDetail ld WHERE ld.book.bookId = :bookId AND ld.returnDate IS NULL AND ld.loan.user.userId = :userId")
    boolean isBookCurrentlyBorrowedByUser(@Param("bookId") Integer bookId, @Param("userId") Integer userId);
    
    // Count currently borrowed books by a user
    @Query("SELECT COUNT(ld) FROM LoanDetail ld WHERE ld.returnDate IS NULL AND ld.loan.user.userId = :userId")
    long countCurrentlyBorrowedBooksByUser(@Param("userId") Integer userId);
    
    // Get loan details that are not returned
    List<LoanDetail> findByLoanUserUserIdAndReturnDateIsNull(Integer userId);
    
    // Đếm số sách đang mượn (chưa trả) của một người dùng
    @Query("SELECT COUNT(ld) FROM LoanDetail ld WHERE ld.loan.user = :user AND ld.returnDate IS NULL")
    int countByLoanUserAndReturnDateIsNull(@Param("user") User user);
    
    // Kiểm tra sách có đang được mượn không
    @Query("SELECT COUNT(ld) > 0 FROM LoanDetail ld WHERE ld.book.bookId = :bookId AND ld.returnDate IS NULL")
    boolean isBookCurrentlyBorrowed(@Param("bookId") Integer bookId);
} 