package com.thaihoangbao.BaoLibrary.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.thaihoangbao.BaoLibrary.entity.User;

/**
 * Lớp CustomUserDetails để sử dụng trong quá trình xác thực với Spring Security
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Lấy vai trò của người dùng và chuyển thành GrantedAuthority
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getMatKhau();
    }

    @Override
    public String getUsername() {
        return user.getTaiKhoan();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "active".equals(user.getTrangThai());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "active".equals(user.getTrangThai());
    }

    /**
     * Lấy ID của người dùng
     */
    public Integer getUserId() {
        return user.getUserId();
    }

    /**
     * Lấy đối tượng User gốc
     */
    public User getUser() {
        return user;
    }
} 