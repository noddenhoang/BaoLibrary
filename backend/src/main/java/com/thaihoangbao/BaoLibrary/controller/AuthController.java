package com.thaihoangbao.BaoLibrary.controller;

import com.thaihoangbao.BaoLibrary.dto.AuthResponse;
import com.thaihoangbao.BaoLibrary.dto.LoginRequest;
import com.thaihoangbao.BaoLibrary.dto.RegisterRequest;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
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
}