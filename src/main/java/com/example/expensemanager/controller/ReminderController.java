package com.example.expensemanager.controller;

import com.example.expensemanager.model.Reminder;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.ReminderRequest;
import com.example.expensemanager.payload.response.MessageResponse;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.IReminderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final IReminderService reminderService;
    private final UserRepository userRepository;

    @Autowired
    public ReminderController(IReminderService reminderService, UserRepository userRepository) {
        this.reminderService = reminderService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Reminder>> getAllReminders() {
        Long userId = getCurrentUserId();
        List<Reminder> reminders = reminderService.getAllRemindersForUser(userId);
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Reminder> getReminderById(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        Reminder reminder = reminderService.getReminderById(id, userId);
        return ResponseEntity.ok(reminder);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN tạo Reminder
    public ResponseEntity<Reminder> createReminder(@Valid @RequestBody ReminderRequest reminderRequest) {
        Long userId = getCurrentUserId();
        Reminder reminder = reminderService.createReminder(reminderRequest, userId);
        return new ResponseEntity<>(reminder, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN cập nhật Reminder
    public ResponseEntity<Reminder> updateReminder(@PathVariable("id") Long id, @Valid @RequestBody ReminderRequest reminderRequest) {
        Long userId = getCurrentUserId();
        Reminder updatedReminder = reminderService.updateReminder(id, reminderRequest, userId);
        return ResponseEntity.ok(updatedReminder);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN xóa Reminder của mình
    public ResponseEntity<MessageResponse> deleteReminder(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        reminderService.deleteReminder(id, userId);
        return ResponseEntity.ok(new MessageResponse("Reminder deleted successfully!"));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found by username: " + username));
        return user.getId();
    }
}