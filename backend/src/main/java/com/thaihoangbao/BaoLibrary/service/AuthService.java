package com.thaihoangbao.BaoLibrary.service;

import com.thaihoangbao.BaoLibrary.dto.AuthResponse;
import com.thaihoangbao.BaoLibrary.dto.LoginRequest;
import com.thaihoangbao.BaoLibrary.dto.RegisterRequest;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import com.thaihoangbao.BaoLibrary.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByTaiKhoan(request.getTaiKhoan())) {
            throw new RuntimeException("Tài khoản đã tồn tại");
        }
        
        // Check if email already exists
        if (request.getEmail() != null && !request.getEmail().isEmpty() && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        
        User user = new User();
        user.setHoTen(request.getHoTen());
        user.setDiaChi(request.getDiaChi());
        user.setEmail(request.getEmail());
        user.setSoDienThoai(request.getSoDienThoai());
        user.setTaiKhoan(request.getTaiKhoan());
        user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        
        // Sử dụng role từ request, với xác thực quyền nếu cần
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.member);
        
        user.setTrangThai("active");
        user.setNgayDangKy(new Date());
        
        User savedUser = userRepository.save(user);
        
        String token = jwtUtil.generateToken(savedUser);
        
        return new AuthResponse(
            token, 
            savedUser.getTaiKhoan(), 
            savedUser.getHoTen(), 
            savedUser.getRole().name()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getTaiKhoan(), 
                request.getMatKhau()
            )
        );
        
        User user = userRepository.findByTaiKhoan(request.getTaiKhoan())
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(
            token, 
            user.getTaiKhoan(), 
            user.getHoTen(), 
            user.getRole().name()
        );
    }
}