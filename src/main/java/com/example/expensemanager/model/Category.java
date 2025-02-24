// Category.java
package com.example.expensemanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Tên category nên unique
    private String name;

    private String description; // Mô tả (optional)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Thêm user_id vào Category
    private User user;

    // Không cần mappedBy ở đây, vì quan hệ đã được định nghĩa ở Transaction.
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets;
    
    // Constructors, getters, and setters (Lombok sẽ tự generate)
}