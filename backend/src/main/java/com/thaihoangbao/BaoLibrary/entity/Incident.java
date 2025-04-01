package com.thaihoangbao.BaoLibrary.entity;

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
@Table(name = "INCIDENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IncidentID")
    private Integer incidentId;
    
    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;
    
    @Column(name = "Title")
    private String title;
    
    @Column(name = "Content")
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "BookID")
    private Book book;
    
    @Column(name = "ReportDate")
    @Temporal(TemporalType.DATE)
    private Date reportDate;
    
    @Column(name = "Status")
    private String status; // "pending", "in_progress", "resolved", "rejected"
    
    @Column(name = "ResolvedDate")
    @Temporal(TemporalType.DATE)
    private Date resolvedDate;
    
    @Column(name = "Resolution")
    private String resolution;
}
