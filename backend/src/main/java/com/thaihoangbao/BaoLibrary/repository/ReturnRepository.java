package com.thaihoangbao.BaoLibrary.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.LoanDetail;
import com.thaihoangbao.BaoLibrary.entity.Return;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Integer> {
    
    // Find return by loan detail
    Optional<Return> findByLoanDetail(LoanDetail loanDetail);
    
    // Find return by loan detail ID
    Optional<Return> findByLoanDetailLoanDetailId(Integer loanDetailId);
    
    // Find returns processed by a specific staff member
    List<Return> findByProcessedBy(Integer processedById);
    
    // Find returns by date range
    List<Return> findByReturnDateBetween(Date startDate, Date endDate);
    
    // Find returns with late fees
    List<Return> findByLateFeeGreaterThan(Double amount);
    
    // Count returns by a specific user for statistical purposes
    @Query("SELECT COUNT(r) FROM Return r JOIN r.loanDetail ld JOIN ld.loan l WHERE l.user.userId = :userId")
    long countReturnsByUser(@Param("userId") Integer userId);
}
