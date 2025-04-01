package com.thaihoangbao.BaoLibrary.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thaihoangbao.BaoLibrary.dto.BranchDto;
import com.thaihoangbao.BaoLibrary.entity.Branch;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.repository.BranchRepository;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import com.thaihoangbao.BaoLibrary.service.BranchService;

@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepository branchRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public List<BranchDto> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BranchDto getBranchById(Integer id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi nhánh với ID: " + id));
        
        return convertToDto(branch);
    }

    @Override
    public BranchDto createBranch(BranchDto branchDto) {
        Branch branch = convertToEntity(branchDto);
        Branch savedBranch = branchRepository.save(branch);
        return convertToDto(savedBranch);
    }

    @Override
    public BranchDto updateBranch(Integer id, BranchDto branchDto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi nhánh với ID: " + id));
        
        branch.setTenChiNhanh(branchDto.getTenChiNhanh());
        branch.setDiaChi(branchDto.getDiaChi());
        branch.setSoDienThoai(branchDto.getSoDienThoai());
        
        // Cập nhật manager nếu có
        if (branchDto.getManagerId() != null) {
            User manager = userRepository.findById(branchDto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người quản lý với ID: " + branchDto.getManagerId()));
            branch.setManager(manager);
        } else {
            branch.setManager(null);
        }
        
        Branch updatedBranch = branchRepository.save(branch);
        return convertToDto(updatedBranch);
    }

    @Override
    public void deleteBranch(Integer id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi nhánh với ID: " + id));
        
        branchRepository.delete(branch);
    }
    
    /**
     * Chuyển đổi Entity thành DTO
     * @param branch Entity chi nhánh
     * @return DTO chi nhánh
     */
    private BranchDto convertToDto(Branch branch) {
        BranchDto dto = new BranchDto();
        dto.setBranchId(branch.getBranchId());
        dto.setTenChiNhanh(branch.getTenChiNhanh());
        dto.setDiaChi(branch.getDiaChi());
        dto.setSoDienThoai(branch.getSoDienThoai());
        
        if (branch.getManager() != null) {
            dto.setManagerId(branch.getManager().getUserId());
        }
        
        return dto;
    }
    
    /**
     * Chuyển đổi DTO thành Entity
     * @param dto DTO chi nhánh
     * @return Entity chi nhánh
     */
    private Branch convertToEntity(BranchDto dto) {
        Branch branch = new Branch();
        branch.setTenChiNhanh(dto.getTenChiNhanh());
        branch.setDiaChi(dto.getDiaChi());
        branch.setSoDienThoai(dto.getSoDienThoai());
        
        if (dto.getManagerId() != null) {
            User manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người quản lý với ID: " + dto.getManagerId()));
            branch.setManager(manager);
        }
        
        return branch;
    }
} 