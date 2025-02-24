package com.example.expensemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);       // Lấy danh sách Budget theo userId
    List<Budget> findByCategoryId(Long categoryId); // Lấy danh sách Budget theo categoryId
    // Thêm các phương thức truy vấn khác nếu cần (ví dụ: theo khoảng thời gian...)
}