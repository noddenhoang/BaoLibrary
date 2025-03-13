package com.thaihoangbao.BaoLibrary.dto;

import com.thaihoangbao.BaoLibrary.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;
    
    private String diaChi;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String soDienThoai;
    
    @NotBlank(message = "Tài khoản không được để trống")
    @Size(min = 4, message = "Tài khoản phải có ít nhất 4 ký tự")
    private String taiKhoan;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhau;
    
    // Thêm trường role
    private User.Role role = User.Role.member; // Mặc định là member nếu không chỉ định
}