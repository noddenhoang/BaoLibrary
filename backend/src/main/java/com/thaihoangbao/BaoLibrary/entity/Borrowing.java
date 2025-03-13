package com.thaihoangbao.BaoLibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    
    // Removed DueDate and ReturnDate as they're in LOANDETAIL table
    // Removed Status as it's not in LOAN table
}