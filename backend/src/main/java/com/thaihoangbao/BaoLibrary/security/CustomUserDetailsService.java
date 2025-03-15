package com.thaihoangbao.BaoLibrary.security;

import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByTaiKhoan(username)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        // Create authorities from user roles
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // Add role as authority (without ROLE_ prefix)
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
        // Also add with ROLE_ prefix for Spring Security's hasRole method
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
            user.getTaiKhoan(),
            user.getMatKhau(),
            user.getTrangThai().equals("active"),
            true, true, true,
            authorities
        );
    }
}