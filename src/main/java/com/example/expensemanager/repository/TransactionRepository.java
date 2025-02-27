package com.example.expensemanager.repository;

import com.example.expensemanager.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    List<Transaction> findByUserId(Long userId); // Thêm phương thức này
    Optional<Transaction> findByIdAndUserId(Long id, Long userId); // Kiểm tra quyền sở hữu
}