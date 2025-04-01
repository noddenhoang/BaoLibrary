package com.thaihoangbao.BaoLibrary.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoangbao.BaoLibrary.dto.IncidentDTO;
import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.security.SecurityHelper;
import com.thaihoangbao.BaoLibrary.service.IncidentService;
import com.thaihoangbao.BaoLibrary.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SecurityHelper securityHelper;
    
    // Tạo báo cáo sự cố mới
    @PostMapping
    public ResponseEntity<IncidentDTO> createIncident(@Valid @RequestBody IncidentDTO incidentDTO) {
        // Nếu không phải admin, chỉ có thể báo cáo cho chính mình
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream()
            .anyMatch(authority -> authority.getAuthority().equals("admin"));
        
        if (!isAdmin) {
            String username = securityHelper.getCurrentUsername();
            Integer userId = userService.getUserByUsername(username).getUserId();
            incidentDTO.setUserId(userId);
        }
        
        return new ResponseEntity<>(incidentService.createIncident(incidentDTO), HttpStatus.CREATED);
    }
    
    // Lấy thông tin báo cáo sự cố theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager') or authentication.principal.username == @userService.getUserById(@incidentService.getIncidentById(#id).userId).taiKhoan")
    public ResponseEntity<IncidentDTO> getIncidentById(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }
    
    // Lấy danh sách tất cả báo cáo sự cố (chỉ admin hoặc manager)
    @GetMapping
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    public PagedResponse<IncidentDTO> getAllIncidents(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "reportDate", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir) {
        return incidentService.getAllIncidents(pageNo, pageSize, sortBy, sortDir);
    }
    
    // Lấy danh sách báo cáo sự cố của một người dùng
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager') or authentication.principal.username == @userService.getUserById(#userId).taiKhoan")
    public PagedResponse<IncidentDTO> getIncidentsByUser(
            @PathVariable(value = "userId") Integer userId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return incidentService.getIncidentsByUser(userId, pageNo, pageSize);
    }
    
    // Lấy danh sách báo cáo sự cố theo trạng thái
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    public PagedResponse<IncidentDTO> getIncidentsByStatus(
            @PathVariable(value = "status") String status,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return incidentService.getIncidentsByStatus(status, pageNo, pageSize);
    }
    
    // Cập nhật trạng thái và giải pháp cho báo cáo sự cố (chỉ admin hoặc manager)
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('manager')")
    public ResponseEntity<IncidentDTO> updateIncidentStatus(
            @PathVariable(value = "id") Integer id,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "resolution", required = false) String resolution) {
        return ResponseEntity.ok(incidentService.updateIncidentStatus(id, status, resolution));
    }
    
    // Xóa báo cáo sự cố (chỉ admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> deleteIncident(@PathVariable(value = "id") Integer id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }
}
