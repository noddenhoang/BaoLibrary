package com.thaihoangbao.BaoLibrary.dto;

import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer userId;
    
    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;
    
    private String diaChi;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String soDienThoai;
    
    @NotBlank(message = "Tài khoản không được để trống")
    @Size(min = 4, message = "Tài khoản phải có ít nhất 4 ký tự")
    private String taiKhoan;
    
    private String role;
    
    private String trangThai;
    
    private Date ngayDangKy;
}
