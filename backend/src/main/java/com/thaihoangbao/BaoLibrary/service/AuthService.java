package com.thaihoangbao.BaoLibrary.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thaihoangbao.BaoLibrary.dto.AuthResponse;
import com.thaihoangbao.BaoLibrary.dto.LoginRequest;
import com.thaihoangbao.BaoLibrary.dto.PasswordResetInitRequest;
import com.thaihoangbao.BaoLibrary.dto.PasswordResetRequest;
import com.thaihoangbao.BaoLibrary.dto.RegisterRequest;
import com.thaihoangbao.BaoLibrary.entity.PasswordResetToken;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.repository.PasswordResetTokenRepository;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import com.thaihoangbao.BaoLibrary.security.JwtUtil;

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
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    // Token validity in hours
    private static final int TOKEN_VALIDITY_HOURS = 24;
    
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
    
    public void initiatePasswordReset(PasswordResetInitRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này"));
        
        // Generate a random token
        String token = UUID.randomUUID().toString();
        
        // Invalidate any existing tokens for this user
        tokenRepository.findByUser(user).forEach(t -> {
            t.setUsed(true);
            tokenRepository.save(t);
        });
        
        // Create a new token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS));
        resetToken.setUsed(false);
        
        tokenRepository.save(resetToken);
        
        // In a production app, you would send an email with the token
        // For now, we'll just log it
        System.out.println("Password reset link: " + token);
    }
    
    public void resetPassword(PasswordResetRequest request) {
        // Find the token
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));
        
        // Check if token is expired or already used
        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new RuntimeException("Token đã hết hạn hoặc đã được sử dụng");
        }
        
        // Check that the token belongs to the user with that email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này"));
        
        if (!resetToken.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Token không khớp với người dùng");
        }
        
        // Update password
        user.setMatKhau(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
    
    public User getUserProfile(String username) {
        return userRepository.findByTaiKhoan(username)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }
}