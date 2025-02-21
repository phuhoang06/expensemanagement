package com.example.expensemanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String accountName; // Tên tài khoản (ví dụ: "Vietcombank", "Ví Momo")

    @Size(max = 50)
    private String accountNumber; // Số tài khoản (optional, có thể không cần thiết nếu chỉ cần tên)

    @Size(max = 50)
    private String bankName; // Tên ngân hàng/ví (optional)

    @Digits(integer=12, fraction=2) // Tối đa 12 chữ số phần nguyên, 2 chữ số phần thập phân
    private BigDecimal balance; // Số dư hiện tại (có thể tính toán từ transactions hoặc lưu trực tiếp)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Tài khoản thuộc về User nào

    // Constructors, Getters, Setters
    public BankAccount() {
    }

    public BankAccount(String accountName, String accountNumber, String bankName, BigDecimal balance, User user) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.balance = balance;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}