// Reminder.java
package com.example.expensemanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
@Getter
@Setter
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime reminderDateTime;

    private boolean isSent; // Đã gửi thông báo hay chưa

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // (Optional) Liên kết với Transaction, nếu nhắc nhở liên quan đến 1 giao dịch cụ thể.
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "transaction_id") // Có thể null
     private Transaction transaction;


}