package com.example.expensemanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserId(Long userId, Pageable pageable); // Phân trang danh sách Transaction theo userId
    List<Transaction> findByCategoryId(Long categoryId);            // Lấy danh sách Transaction theo categoryId
    // Thêm các phương thức truy vấn khác nếu cần (ví dụ: theo khoảng thời gian, theo loại giao dịch...)
}