package com.thaihoangbao.BaoLibrary.service.impl;

import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.dto.UserDTO;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import com.thaihoangbao.BaoLibrary.repository.ViolationRepository;
import com.thaihoangbao.BaoLibrary.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ViolationRepository violationRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByTaiKhoan(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserDTO> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> usersPage = userRepository.findAll(pageable);
        
        List<User> users = usersPage.getContent();
        List<UserDTO> content = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                content,
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.isLast()
        );
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Kiểm tra trùng lặp
        if (userRepository.existsByTaiKhoan(userDTO.getTaiKhoan())) {
            throw new RuntimeException("Tài khoản đã tồn tại");
        }
        
        if (userDTO.getEmail() != null && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        
        User user = new User();
        user.setHoTen(userDTO.getHoTen());
        user.setDiaChi(userDTO.getDiaChi());
        user.setEmail(userDTO.getEmail());
        user.setSoDienThoai(userDTO.getSoDienThoai());
        user.setTaiKhoan(userDTO.getTaiKhoan());
        
        // Mã hóa mật khẩu nếu có
        if (userDTO.getTaiKhoan() != null) {
            user.setMatKhau(passwordEncoder.encode("12345678")); // Mật khẩu mặc định
        }
        
        // Thiết lập role nếu có, mặc định là member
        if (userDTO.getRole() != null) {
            user.setRole(User.Role.valueOf(userDTO.getRole()));
        } else {
            user.setRole(User.Role.member);
        }
        
        // Thiết lập trạng thái, mặc định là active
        if (userDTO.getTrangThai() != null) {
            user.setTrangThai(userDTO.getTrangThai());
        } else {
            user.setTrangThai("active");
        }
        
        // Thiết lập ngày đăng ký, mặc định là ngày hiện tại
        if (userDTO.getNgayDangKy() != null) {
            user.setNgayDangKy(userDTO.getNgayDangKy());
        } else {
            user.setNgayDangKy(new Date());
        }
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        // Cập nhật các trường thông tin
        if (userDTO.getHoTen() != null) {
            user.setHoTen(userDTO.getHoTen());
        }
        
        if (userDTO.getDiaChi() != null) {
            user.setDiaChi(userDTO.getDiaChi());
        }
        
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        
        if (userDTO.getSoDienThoai() != null) {
            user.setSoDienThoai(userDTO.getSoDienThoai());
        }
        
        if (userDTO.getRole() != null) {
            user.setRole(User.Role.valueOf(userDTO.getRole()));
        }
        
        if (userDTO.getTrangThai() != null) {
            user.setTrangThai(userDTO.getTrangThai());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void setUserStatus(Integer id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setTrangThai(status);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkIfUserCanBorrow(Integer userId) {
        // Kiểm tra trạng thái tài khoản
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        if (!"active".equals(user.getTrangThai())) {
            return false;
        }
        
        // Kiểm tra các khoản phạt chưa thanh toán
        BigDecimal unpaidFines = violationRepository.findTotalUnpaidFinesByUserId(userId);
        if (unpaidFines != null && unpaidFines.compareTo(BigDecimal.ZERO) > 0) {
            return false;
        }
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
    
    // Helper method để chuyển đổi Entity thành DTO
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setHoTen(user.getHoTen());
        userDTO.setDiaChi(user.getDiaChi());
        userDTO.setEmail(user.getEmail());
        userDTO.setSoDienThoai(user.getSoDienThoai());
        userDTO.setTaiKhoan(user.getTaiKhoan());
        userDTO.setRole(user.getRole().toString());
        userDTO.setTrangThai(user.getTrangThai());
        userDTO.setNgayDangKy(user.getNgayDangKy());
        return userDTO;
    }
}
