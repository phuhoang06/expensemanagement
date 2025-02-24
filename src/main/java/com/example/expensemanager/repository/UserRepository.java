package com.example.expensemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Tìm User theo username
    Boolean existsByUsername(String username);      // Kiểm tra username đã tồn tại chưa
    Boolean existsByEmail(String email);           // Kiểm tra email đã tồn tại chưa
}