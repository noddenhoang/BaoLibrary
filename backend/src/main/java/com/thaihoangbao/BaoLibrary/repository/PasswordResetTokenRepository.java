package com.thaihoangbao.BaoLibrary.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.PasswordResetToken;
import com.thaihoangbao.BaoLibrary.entity.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    List<PasswordResetToken> findByUser(User user);
    
    Optional<PasswordResetToken> findByUserAndToken(User user, String token);
    
    void deleteByUser(User user);
} 