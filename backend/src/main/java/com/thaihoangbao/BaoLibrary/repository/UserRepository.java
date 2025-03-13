package com.thaihoangbao.BaoLibrary.repository;

import com.thaihoangbao.BaoLibrary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByTaiKhoan(String taiKhoan);
    boolean existsByTaiKhoan(String taiKhoan);
    boolean existsByEmail(String email);
}