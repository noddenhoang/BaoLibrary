package com.thaihoangbao.BaoLibrary.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LOAN") // Changed from "BORROWING" to "LOAN" to match the database schema
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LoanID") // Changed from "BorrowID" to "LoanID"
    private Integer borrowId;
    
    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "BranchID", nullable = false) // Added BranchID as per schema
    private Branch branch; // Need to create Branch entity
    
    @Column(name = "NgayMuon", nullable = false) // Changed from "BorrowDate" to "NgayMuon"
    @Temporal(TemporalType.DATE)
    private Date borrowDate;
    
    // Added relationship with LoanDetail
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LoanDetail> loanDetails = new HashSet<>();
}