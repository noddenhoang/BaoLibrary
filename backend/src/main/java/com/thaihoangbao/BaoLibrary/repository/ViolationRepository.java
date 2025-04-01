package com.thaihoangbao.BaoLibrary.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.LoanDetail;
import com.thaihoangbao.BaoLibrary.entity.Violation;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Integer> {
    
    // Find violations by loan detail
    List<Violation> findByLoanDetail(LoanDetail loanDetail);
    
    // Find violations by loan detail ID
    List<Violation> findByLoanDetailLoanDetailId(Integer loanDetailId);
    
    // Find violations by type
    List<Violation> findByViolationType(String violationType);
    
    // Find violations by status
    List<Violation> findByStatus(String status);
    
    // Find violations by date range
    List<Violation> findByViolationDateBetween(Date startDate, Date endDate);
    
    // Find pending violations for a specific user
    @Query("SELECT v FROM Violation v JOIN v.loanDetail ld JOIN ld.loan l WHERE l.user.userId = :userId AND v.status = 'pending'")
    List<Violation> findPendingViolationsByUserId(@Param("userId") Integer userId);
    
    // Find total fine amount for a user
    @Query("SELECT SUM(v.fineAmount) FROM Violation v JOIN v.loanDetail ld JOIN ld.loan l WHERE l.user.userId = :userId AND v.status = :status")
    Double getTotalFineAmountByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status);
    
    // Find total unpaid fines by user ID
    @Query("SELECT SUM(v.fineAmount) FROM Violation v JOIN v.loanDetail ld JOIN ld.loan l WHERE l.user.userId = :userId AND v.status = 'pending'")
    BigDecimal findTotalUnpaidFinesByUserId(@Param("userId") Integer userId);
    
    // Count violations in a date range
    long countByViolationDateBetween(Date startDate, Date endDate);
    
    // Sum of paid fines in a date range
    @Query("SELECT SUM(v.fineAmount) FROM Violation v WHERE v.status = 'paid' AND v.violationDate BETWEEN :startDate AND :endDate")
    BigDecimal sumPaidFinesByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
