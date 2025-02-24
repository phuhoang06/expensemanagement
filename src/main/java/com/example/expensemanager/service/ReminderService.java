package com.example.expensemanager.service;

import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.payload.request.ReminderRequest;
import com.example.expensemanager.repository.ReminderRepository;
import com.example.expensemanager.repository.TransactionRepository;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.IReminderService; // Import đúng interface từ 'interfaces'
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReminderService implements IReminderService {

    private final ReminderRepository reminderRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository; // Cần để validate user tồn tại

    @Autowired
    public ReminderService(ReminderRepository reminderRepository, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.reminderRepository = reminderRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Reminder> getAllRemindersForUser(Long userId) {
        validateUserExists(userId);
        return reminderRepository.findByUserId(userId);
    }

    @Override
    public Reminder getReminderById(Long id, Long userId) {
        validateUserExists(userId);
        return reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
    }

    @Override
    @Transactional
    public Reminder createReminder(ReminderRequest reminderRequest, Long userId) {
        validateUserExists(userId);
        Reminder reminder = new Reminder();
        reminder.setDescription(reminderRequest.getDescription());
        reminder.setDueDate(reminderRequest.getDueDate());
        reminder.setUser(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
        if (reminderRequest.getTransactionId() != null) {
            transactionRepository.findById(reminderRequest.getTransactionId())
                    .ifPresent(reminder::setTransaction); // Set Transaction nếu có và tồn tại
        }

        return reminderRepository.save(reminder);
    }

    @Override
    @Transactional
    public Reminder updateReminder(Long id, ReminderRequest reminderRequest, Long userId) {
        validateUserExists(userId);
        Reminder existingReminder = getReminderById(id, userId); // Kiểm tra reminder tồn tại và thuộc về user

        existingReminder.setDescription(reminderRequest.getDescription());
        existingReminder.setDueDate(reminderRequest.getDueDate());
        if (reminderRequest.getTransactionId() != null) {
            transactionRepository.findById(reminderRequest.getTransactionId())
                    .ifPresent(existingReminder::setTransaction);
        } else {
            existingReminder.setTransaction(null); // Nếu transactionId không được gửi lên khi update thì set null
        }

        return reminderRepository.save(existingReminder);
    }

    @Override
    @Transactional
    public void deleteReminder(Long id, Long userId) {
        validateUserExists(userId);
        Reminder reminder = getReminderById(id, userId); // Kiểm tra reminder tồn tại và thuộc về user
        reminderRepository.delete(reminder);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }
}