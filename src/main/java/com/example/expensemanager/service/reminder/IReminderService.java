package com.example.expensemanager.service.reminder;

import com.example.expensemanager.payload.request.ReminderRequest;
import com.example.expensemanager.payload.response.ReminderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IReminderService {
    Page<ReminderResponse> getAllRemindersByUserId(Long userId, Pageable pageable);
    List<ReminderResponse> getAllRemindersByUserId(Long userId);
    ReminderResponse createReminder(Long userId, ReminderRequest reminderRequest);
    ReminderResponse updateReminder(Long userId, Long reminderId, ReminderRequest reminderRequest);
    boolean deleteReminder(Long userId, Long reminderId);
    ReminderResponse getReminderById(Long userId, Long reminderId);
}