package com.thaihoangbao.BaoLibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BRANCH")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BranchID")
    private Integer branchId;
    
    @Column(name = "TenChiNhanh", nullable = false)
    private String tenChiNhanh;
    
    @Column(name = "DiaChi")
    private String diaChi;
    
    @Column(name = "SoDienThoai")
    private String soDienThoai;
    
    @ManyToOne
    @JoinColumn(name = "ManagerID")
    private User manager;

    public String getBranchName() {
        return this.tenChiNhanh;
    }
}
