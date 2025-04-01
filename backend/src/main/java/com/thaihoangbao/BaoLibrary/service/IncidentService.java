package com.thaihoangbao.BaoLibrary.service;

import com.thaihoangbao.BaoLibrary.dto.IncidentDTO;
import com.thaihoangbao.BaoLibrary.dto.PagedResponse;

public interface IncidentService {
    IncidentDTO createIncident(IncidentDTO incidentDTO);
    
    IncidentDTO getIncidentById(Integer id);
    
    PagedResponse<IncidentDTO> getAllIncidents(int pageNo, int pageSize, String sortBy, String sortDir);
    
    PagedResponse<IncidentDTO> getIncidentsByUser(Integer userId, int pageNo, int pageSize);
    
    PagedResponse<IncidentDTO> getIncidentsByStatus(String status, int pageNo, int pageSize);
    
    IncidentDTO updateIncidentStatus(Integer id, String status, String resolution);
    
    void deleteIncident(Integer id);
}
