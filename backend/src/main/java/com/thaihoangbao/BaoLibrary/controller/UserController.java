package com.thaihoangbao.BaoLibrary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.dto.UserDTO;
import com.thaihoangbao.BaoLibrary.security.SecurityHelper;
import com.thaihoangbao.BaoLibrary.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private SecurityHelper securityHelper;
    
    // Lấy thông tin người dùng theo ID (chỉ admin hoặc chính người dùng đó)
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin') or authentication.principal.username == @userService.getUserById(#id).taiKhoan")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    // Lấy thông tin người dùng hiện tại
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        String username = securityHelper.getCurrentUsername();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
    
    // Lấy danh sách tất cả người dùng (chỉ admin)
    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public PagedResponse<UserDTO> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "userId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        return userService.getAllUsers(pageNo, pageSize, sortBy, sortDir);
    }
    
    // Tạo người dùng mới (chỉ admin)
    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }
    
    // Cập nhật thông tin người dùng (chỉ admin hoặc chính người dùng đó)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin') or authentication.principal.username == @userService.getUserById(#id).taiKhoan")
    public ResponseEntity<UserDTO> updateUser(@PathVariable(value = "id") Integer id, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }
    
    // Xóa người dùng (chỉ admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "id") Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // Thay đổi trạng thái người dùng (chỉ admin)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> setUserStatus(
            @PathVariable(value = "id") Integer id,
            @RequestParam(value = "status") String status) {
        userService.setUserStatus(id, status);
        return ResponseEntity.ok().build();
    }
    
    // Kiểm tra người dùng có thể mượn sách hay không
    @GetMapping("/{id}/can-borrow")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager') or authentication.principal.username == @userService.getUserById(#id).taiKhoan")
    public ResponseEntity<Boolean> checkIfUserCanBorrow(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(userService.checkIfUserCanBorrow(id));
    }
}
