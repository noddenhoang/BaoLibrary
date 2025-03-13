package com.thaihoangbao.BaoLibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;
    
    @Column(name = "HoTen", nullable = false)
    private String hoTen;
    
    @Column(name = "DiaChi")
    private String diaChi;
    
    @Column(name = "Email", unique = true)
    private String email;
    
    @Column(name = "SoDienThoai")
    private String soDienThoai;
    
    @Column(name = "TaiKhoan", unique = true, nullable = false)
    private String taiKhoan;
    
    @Column(name = "MatKhau", nullable = false)
    private String matKhau;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "Role")
    private Role role = Role.member;
    
    @Column(name = "TrangThai")
    private String trangThai = "active";
    
    @Column(name = "NgayDangKy")
    @Temporal(TemporalType.DATE)
    private Date ngayDangKy = new Date();
    
    public enum Role {
        member, manager, admin
    }
}