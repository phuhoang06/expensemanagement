package com.example.expensemanager.repository;

import com.example.expensemanager.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUserId(Long userId); // Lấy danh sách Reminder theo userId
    // Thêm các phương thức truy vấn khác nếu cần (ví dụ: theo trạng thái, theo thời hạn...)
}