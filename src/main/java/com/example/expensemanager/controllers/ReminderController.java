package com.example.expensemanager.controllers;

import com.example.expensemanager.payload.request.ReminderRequest;
import com.example.expensemanager.payload.response.ReminderResponse;
import com.example.expensemanager.service.reminder.IReminderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/reminders")
public class ReminderController {
 @Autowired
    private IReminderService reminderService;

    @GetMapping
    public ResponseEntity<Page<ReminderResponse>> getAllReminderByUserId(
            @PathVariable Long userId,
            @PageableDefault(sort = "reminderDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReminderResponse> reminders = reminderService.getAllRemindersByUserId(userId, pageable);
        return ResponseEntity.ok(reminders);
    }
    @GetMapping("/all") // Dành cho trường hợp muốn lấy tất cả, không phân trang
    public ResponseEntity<List<ReminderResponse>> getAllReminders(@PathVariable Long userId){
        List<ReminderResponse> reminders = reminderService.getAllRemindersByUserId(userId);
        return  ResponseEntity.ok(reminders);
    }

    @GetMapping("/{reminderId}")
    public ResponseEntity<ReminderResponse> getReminderById(
            @PathVariable Long userId,
            @PathVariable Long reminderId) {
        ReminderResponse reminder = reminderService.getReminderById(userId, reminderId);
        return ResponseEntity.ok(reminder);
    }

    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(
            @PathVariable Long userId,
            @Valid @RequestBody ReminderRequest reminderRequest) {
        ReminderResponse createdReminder = reminderService.createReminder(userId, reminderRequest);
        return new ResponseEntity<>(createdReminder, HttpStatus.CREATED);
    }

    @PutMapping("/{reminderId}")
    public ResponseEntity<ReminderResponse> updateReminder(@PathVariable Long userId, @PathVariable Long reminderId,
            @Valid @RequestBody ReminderRequest reminderRequest) {
        ReminderResponse updatedReminder = reminderService.updateReminder(userId, reminderId, reminderRequest);
        return ResponseEntity.ok(updatedReminder);
    }
    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long userId, @PathVariable Long reminderId){
        reminderService.deleteReminder(userId,reminderId);
        return ResponseEntity.noContent().build();
    }
}