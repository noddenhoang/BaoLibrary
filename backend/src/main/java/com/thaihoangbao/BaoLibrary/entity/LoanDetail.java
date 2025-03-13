package com.thaihoangbao.BaoLibrary.entity;

import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LOANDETAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LoanDetailID")
    private Integer loanDetailId;
    
    @ManyToOne
    @JoinColumn(name = "LoanID", nullable = false)
    private Borrowing loan;
    
    @ManyToOne
    @JoinColumn(name = "BookID", nullable = false)
    private Book book;
    
    @Column(name = "NgayDenHan")
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    
    @Column(name = "NgayTra")
    @Temporal(TemporalType.DATE)
    private Date returnDate;
    
    @OneToOne(mappedBy = "loanDetail", cascade = CascadeType.ALL)
    private Return returnRecord;
} 