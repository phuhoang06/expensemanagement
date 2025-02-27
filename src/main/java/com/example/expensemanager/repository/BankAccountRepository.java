package com.example.expensemanager.repository;

import com.example.expensemanager.model.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Page<BankAccount> findByUserId(Long userId, Pageable pageable);
    List<BankAccount> findByUserId(Long userId);
    Optional<BankAccount> findByIdAndUserId(Long id, Long userId);
     Optional<BankAccount> findByAccountNameAndUserId(String accountName, Long userId);
}