package com.thaihoangbao.BaoLibrary.service.impl;

import com.thaihoangbao.BaoLibrary.dto.IncidentDTO;
import com.thaihoangbao.BaoLibrary.dto.PagedResponse;
import com.thaihoangbao.BaoLibrary.entity.Book;
import com.thaihoangbao.BaoLibrary.entity.Incident;
import com.thaihoangbao.BaoLibrary.entity.User;
import com.thaihoangbao.BaoLibrary.exception.ResourceNotFoundException;
import com.thaihoangbao.BaoLibrary.repository.BookRepository;
import com.thaihoangbao.BaoLibrary.repository.IncidentRepository;
import com.thaihoangbao.BaoLibrary.repository.UserRepository;
import com.thaihoangbao.BaoLibrary.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookRepository bookRepository;

    @Override
    @Transactional
    public IncidentDTO createIncident(IncidentDTO incidentDTO) {
        User user = userRepository.findById(incidentDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + incidentDTO.getUserId()));
        
        Incident incident = new Incident();
        incident.setUser(user);
        incident.setTitle(incidentDTO.getTitle());
        incident.setContent(incidentDTO.getContent());
        
        if (incidentDTO.getBookId() != null) {
            Book book = bookRepository.findById(incidentDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + incidentDTO.getBookId()));
            incident.setBook(book);
        }
        
        incident.setReportDate(new Date());
        incident.setStatus("pending");
        
        Incident savedIncident = incidentRepository.save(incident);
        return convertToDTO(savedIncident);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentDTO getIncidentById(Integer id) {
        Incident incident = incidentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + id));
        return convertToDTO(incident);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<IncidentDTO> getAllIncidents(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Incident> incidentsPage = incidentRepository.findAll(pageable);
        
        List<Incident> incidents = incidentsPage.getContent();
        List<IncidentDTO> content = incidents.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(
            content,
            incidentsPage.getNumber(),
            incidentsPage.getSize(),
            incidentsPage.getTotalElements(),
            incidentsPage.getTotalPages(),
            incidentsPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<IncidentDTO> getIncidentsByUser(Integer userId, int pageNo, int pageSize) {
        // Kiểm tra người dùng tồn tại
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Incident> incidentsPage = incidentRepository.findByUserUserId(userId, pageable);
        
        List<Incident> incidents = incidentsPage.getContent();
        List<IncidentDTO> content = incidents.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(
            content,
            incidentsPage.getNumber(),
            incidentsPage.getSize(),
            incidentsPage.getTotalElements(),
            incidentsPage.getTotalPages(),
            incidentsPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<IncidentDTO> getIncidentsByStatus(String status, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Incident> incidentsPage = incidentRepository.findByStatus(status, pageable);
        
        List<Incident> incidents = incidentsPage.getContent();
        List<IncidentDTO> content = incidents.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(
            content,
            incidentsPage.getNumber(),
            incidentsPage.getSize(),
            incidentsPage.getTotalElements(),
            incidentsPage.getTotalPages(),
            incidentsPage.isLast()
        );
    }

    @Override
    @Transactional
    public IncidentDTO updateIncidentStatus(Integer id, String status, String resolution) {
        Incident incident = incidentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + id));
        
        incident.setStatus(status);
        incident.setResolution(resolution);
        
        if ("resolved".equals(status)) {
            incident.setResolvedDate(new Date());
        }
        
        Incident updatedIncident = incidentRepository.save(incident);
        return convertToDTO(updatedIncident);
    }

    @Override
    @Transactional
    public void deleteIncident(Integer id) {
        Incident incident = incidentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + id));
        incidentRepository.delete(incident);
    }
    
    // Helper method để chuyển đổi Entity thành DTO
    private IncidentDTO convertToDTO(Incident incident) {
        IncidentDTO incidentDTO = new IncidentDTO();
        incidentDTO.setIncidentId(incident.getIncidentId());
        incidentDTO.setUserId(incident.getUser().getUserId());
        incidentDTO.setUserName(incident.getUser().getHoTen());
        incidentDTO.setTitle(incident.getTitle());
        incidentDTO.setContent(incident.getContent());
        
        if (incident.getBook() != null) {
            incidentDTO.setBookId(incident.getBook().getBookId());
            incidentDTO.setBookTitle(incident.getBook().getTuaSach());
        }
        
        incidentDTO.setReportDate(incident.getReportDate());
        incidentDTO.setStatus(incident.getStatus());
        incidentDTO.setResolvedDate(incident.getResolvedDate());
        incidentDTO.setResolution(incident.getResolution());
        
        return incidentDTO;
    }
} 