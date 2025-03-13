package com.thaihoangbao.BaoLibrary.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RETURN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Return {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReturnID")
    private Integer returnId;
    
    @OneToOne
    @JoinColumn(name = "LoanDetailID", nullable = false)
    private LoanDetail loanDetail;
    
    @Column(name = "ReturnDate")
    @Temporal(TemporalType.DATE)
    private Date returnDate;
    
    @Column(name = "LateFee")
    private BigDecimal lateFee;
    
    @Column(name = "BookCondition")
    private String bookCondition;
    
    @Column(name = "Comments")
    private String comments;
    
    @Column(name = "ProcessedBy")
    private Integer processedBy; // UserId of the staff who processed the return
}
