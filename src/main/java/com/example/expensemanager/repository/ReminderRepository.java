package com.example.expensemanager.repository;

import com.example.expensemanager.model.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Page<Reminder> findByUserId(Long userId, Pageable pageable);
    List<Reminder> findByUserId(Long userId);
    Optional<Reminder> findByIdAndUserId(Long id, Long userId);
}