package com.thaihoangbao.BaoLibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "BORROWING")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BorrowID")
    private Integer borrowId;
    
    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "BookID", nullable = false)
    private Book book;
    
    @Column(name = "BorrowDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date borrowDate;
    
    @Column(name = "DueDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    
    @Column(name = "ReturnDate")
    @Temporal(TemporalType.DATE)
    private Date returnDate;
    
    @Column(name = "Status")
    private String status; // BORROWED, RETURNED, OVERDUE
}