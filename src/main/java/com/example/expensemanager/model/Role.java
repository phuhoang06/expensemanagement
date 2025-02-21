package com.example.expensemanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles") // Tên bảng là "roles"
@Getter
@Setter
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Lưu trữ enum name dưới dạng String trong database
    @Column(length = 20)
    private ERole name; // Tên vai trò (sử dụng enum ERole)

    public Role(ERole name) {
        this.name = name;
    }
}