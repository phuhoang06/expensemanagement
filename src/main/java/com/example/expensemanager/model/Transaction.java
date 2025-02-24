// Transaction.java
package com.example.expensemanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;
    
    private LocalDateTime createdAt; // Thêm trường thời gian tạo
    private LocalDateTime updatedAt; // Thêm trường thời gian cập nhật

    @ManyToOne(fetch = FetchType.LAZY) // Quan trọng: LAZY loading
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id") // Có thể null (cho tiền mặt)
    private BankAccount bankAccount;

    @PrePersist // Trước khi lưu
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate // Trước khi cập nhật
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}