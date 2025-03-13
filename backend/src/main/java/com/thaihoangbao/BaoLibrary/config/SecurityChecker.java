package com.thaihoangbao.BaoLibrary.config;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Lớp trợ giúp kiểm tra quyền truy cập và xác thực
 */
@Component
public class SecurityChecker {

    /**
     * Kiểm tra xem người dùng đã đăng nhập hay chưa
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser");
    }
    
    /**
     * Kiểm tra xem người dùng có quyền cụ thể không
     */
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
    
    /**
     * Kiểm tra xem người dùng có bất kỳ quyền nào trong danh sách không
     */
    public boolean hasAnyAuthority(String... authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();
        for (String authority : authorities) {
            if (userAuthorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority))) {
                return true;
            }
        }
        
        return false;
    }
} 