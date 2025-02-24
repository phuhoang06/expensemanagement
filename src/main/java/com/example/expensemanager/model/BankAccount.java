// BankAccount.java
package com.example.expensemanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName; // Tên tài khoản (ví dụ: "Vietcombank", "Tiền mặt")

    @Column(nullable = false)
    private BigDecimal balance; // Số dư

    private String description;  //(optional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;  // Các giao dịch liên quan đến tài khoản này.

}