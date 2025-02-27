package com.example.expensemanager.service.interfaces;

import com.example.expensemanager.model.User;
import java.util.Optional;

public interface IUserService {
    Optional<User> findByUsername(String username); // Tìm user theo username
    Boolean existsByUsername(String username);     // Kiểm tra username đã tồn tại chưa
    Boolean existsByEmail(String email);         // Kiểm tra email đã tồn tại chưa
    User save(User user);                           // Lưu User vào database
}