package com.thaihoangbao.BaoLibrary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.Incident;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Integer> {
    Page<Incident> findByUserUserId(Integer userId, Pageable pageable);
    
    Page<Incident> findByStatus(String status, Pageable pageable);
}
