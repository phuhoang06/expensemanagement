package com.example.expensemanager.repository;

import com.example.expensemanager.model.ERole;
import com.example.expensemanager.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Đánh dấu là Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name); // Phương thức tìm Role theo tên (ERole)
}