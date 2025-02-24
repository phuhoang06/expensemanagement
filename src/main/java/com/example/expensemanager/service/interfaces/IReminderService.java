package com.example.expensemanager.service.interfaces;

import com.example.expensemanager.payload.request.ReminderRequest;

import java.util.List;

public interface IReminderService {
    List<Reminder> getAllRemindersForUser(Long userId);
    Reminder getReminderById(Long id, Long userId);
    Reminder createReminder(ReminderRequest reminderRequest, Long userId);
    Reminder updateReminder(Long id, ReminderRequest reminderRequest, Long userId);
    void deleteReminder(Long id, Long userId);
    // Các phương thức service khác liên quan đến Reminder
}