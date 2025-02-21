package com.example.expensemanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String description; // Mô tả nhắc nhở

    @NotNull
    private LocalDateTime dueDate; // Thời hạn thanh toán (Ngày giờ) - LocalDateTime cho cả ngày và giờ

    private boolean isPaid = false; // Trạng thái đã thanh toán hay chưa (mặc định chưa thanh toán)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction; // Nhắc nhở liên kết với Transaction nào (optional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Nhắc nhở thuộc về User nào

    // Constructors, Getters, Setters
    public Reminder() {
    }

    public Reminder(String description, LocalDateTime dueDate, boolean isPaid, Transaction transaction, User user) {
        this.description = description;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
        this.transaction = transaction;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}