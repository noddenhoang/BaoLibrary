package com.thaihoangbao.BaoLibrary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoangbao.BaoLibrary.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByTaiKhoan(String taiKhoan);
    boolean existsByTaiKhoan(String taiKhoan);
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    Optional<User> findByEmail(String email);
    Optional<User> findBySoDienThoai(String soDienThoai);
}