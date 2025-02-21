package com.example.expensemanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users") // Đặt tên bảng là "users" để tránh trùng với từ khóa SQL "user"
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // Tên đăng nhập, phải là duy nhất

    @Column(nullable = false)
    private String password; // Mật khẩu đã mã hóa

    @Column(nullable = false)
    private String email;    // Email người dùng

    // Các trường thông tin người dùng khác (ví dụ: firstName, lastName, ...)
    private String firstName;
    private String lastName;

    @ManyToMany(fetch = FetchType.LAZY) // Quan hệ Many-to-Many với Role, FetchType.LAZY để tránh load roles khi không cần thiết
    @JoinTable(name = "user_roles", // Bảng trung gian để lưu quan hệ Many-to-Many
            joinColumns = @JoinColumn(name = "user_id"), // Khóa ngoại tham chiếu đến bảng users
            inverseJoinColumns = @JoinColumn(name = "role_id")) // Khóa ngoại tham chiếu đến bảng roles
    private Set<Role> roles = new HashSet<>(); // Set các vai trò của người dùng

    // Constructors
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}