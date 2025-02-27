package com.example.expensemanager.service.reminder;

import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.model.Reminder;
import com.example.expensemanager.model.Transaction;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.ReminderRequest;
import com.example.expensemanager.payload.response.ReminderResponse;
import com.example.expensemanager.repository.ReminderRepository;
import com.example.expensemanager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService implements IReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private TransactionRepository transactionRepository; // For optional Transaction association

    @Override
    @Transactional(readOnly = true)
    public Page<ReminderResponse> getAllRemindersByUserId(Long userId, Pageable pageable) {
        Page<Reminder> reminders = reminderRepository.findByUserId(userId, pageable);
        return reminders.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> getAllRemindersByUserId(Long userId) {
        List<Reminder> reminders =  reminderRepository.findByUserId(userId);
        return reminders.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    @Override
    @Transactional
    public ReminderResponse createReminder(Long userId, ReminderRequest reminderRequest) {
        Reminder reminder = new Reminder();
        reminder.setMessage(reminderRequest.getMessage());
        reminder.setReminderDateTime(reminderRequest.getReminderDateTime());
        reminder.setSent(false); // Initially, the reminder is not sent.

        reminder.setUser(new User(userId));

        // Optional: Associate with a Transaction
        if (reminderRequest.getTransactionId() != null) {
             Transaction transaction = transactionRepository.findByIdAndUserId(reminderRequest.getTransactionId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or does not belong to user"));
            reminder.setTransaction(transaction);
        }

        Reminder savedReminder = reminderRepository.save(reminder);
        return convertToResponse(savedReminder);
    }

    @Override
    @Transactional
    public ReminderResponse updateReminder(Long userId, Long reminderId, ReminderRequest reminderRequest) {
        Reminder reminder = reminderRepository.findByIdAndUserId(reminderId, userId)
              .orElseThrow(() -> new ResourceNotFoundException("Reminder not found or does not belong to user"));

        reminder.setMessage(reminderRequest.getMessage());
        reminder.setReminderDateTime(reminderRequest.getReminderDateTime());

        // Optional: Update Transaction association
        if (reminderRequest.getTransactionId() != null) {
            Transaction transaction = transactionRepository.findByIdAndUserId(reminderRequest.getTransactionId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or does not belong to user"));
            reminder.setTransaction(transaction);
        } else {
             reminder.setTransaction(null); // Remove the association
        }

        Reminder updatedReminder = reminderRepository.save(reminder);
        return convertToResponse(updatedReminder);
    }

    @Override
    @Transactional
    public boolean deleteReminder(Long userId, Long reminderId) {
       Reminder reminder = reminderRepository.findByIdAndUserId(reminderId, userId)
              .orElseThrow(() -> new ResourceNotFoundException("Reminder not found or does not belong to user"));
        reminderRepository.delete(reminder);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ReminderResponse getReminderById(Long userId, Long reminderId){
          Reminder reminder = reminderRepository.findByIdAndUserId(reminderId, userId)
              .orElseThrow(() -> new ResourceNotFoundException("Reminder not found or does not belong to user"));
          return convertToResponse(reminder);
    }

    private ReminderResponse convertToResponse(Reminder reminder) {
        ReminderResponse response = new ReminderResponse();
        response.setId(reminder.getId());
        response.setMessage(reminder.getMessage());
        response.setReminderDateTime(reminder.getReminderDateTime());
        response.setSent(reminder.isSent());
        if(reminder.getTransaction() != null) {
            response.setTransactionId(reminder.getTransaction().getId());
        }
        return response;
    }
}