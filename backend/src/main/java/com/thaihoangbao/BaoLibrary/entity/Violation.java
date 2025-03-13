package com.thaihoangbao.BaoLibrary.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "VIOLATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ViolationID")
    private Integer violationId;
    
    @ManyToOne
    @JoinColumn(name = "LoanDetailID", nullable = false)
    private LoanDetail loanDetail;
    
    @Column(name = "LoaiViPham")
    private String violationType; // "late_return", "damaged_book", "lost_book"
    
    @Column(name = "SoTienPhat")
    private BigDecimal fineAmount;
    
    @Column(name = "NgayViPham")
    @Temporal(TemporalType.DATE)
    private Date violationDate;
    
    @Column(name = "TrangThai")
    private String status; // "pending", "paid", "waived"
}
