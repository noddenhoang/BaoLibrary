package com.thaihoangbao.BaoLibrary.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoangbao.BaoLibrary.dto.AuthResponse;
import com.thaihoangbao.BaoLibrary.dto.LoginRequest;
import com.thaihoangbao.BaoLibrary.dto.PasswordResetInitRequest;
import com.thaihoangbao.BaoLibrary.dto.PasswordResetRequest;
import com.thaihoangbao.BaoLibrary.dto.RegisterRequest;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.security.SecurityHelper;
import com.thaihoangbao.BaoLibrary.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private SecurityHelper securityHelper;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Chỉ cho phép tạo người dùng thường qua API đăng ký công khai
        if (request.getRole() != User.Role.member) {
            request.setRole(User.Role.member); // Luôn đặt role là member cho đăng ký công khai
        }
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }
    
    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // Chỉ admin mới có quyền tạo tài khoản với role khác
    public ResponseEntity<AuthResponse> registerByAdmin(@Valid @RequestBody RegisterRequest request) {
        // Ở endpoint này, role trong request sẽ được giữ nguyên vì người gọi là admin
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody PasswordResetInitRequest request) {
        authService.initiatePasswordReset(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Yêu cầu đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra email của bạn.");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Mật khẩu đã được đặt lại thành công.");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getProfile() {
        String username = securityHelper.getCurrentUsername();
        User user = authService.getUserProfile(username);
        return ResponseEntity.ok(user);
    }
}