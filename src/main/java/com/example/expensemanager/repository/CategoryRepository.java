package com.example.expensemanager.repository;

import com.example.expensemanager.model.Category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndUserId(String name, Long userId); // Kiểm tra unique name
     List<Category> findByUserId(Long userId);
     Optional<Category> findByIdAndUserId(Long id, Long userId);
}