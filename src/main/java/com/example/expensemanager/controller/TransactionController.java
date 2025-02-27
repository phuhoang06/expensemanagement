package com.example.expensemanager.controller;

import com.example.expensemanager.model.Transaction;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.TransactionRequest;
import com.example.expensemanager.payload.response.MessageResponse;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.ITransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final ITransactionService transactionService;
    private final UserRepository userRepository;

    @Autowired
    public TransactionController(ITransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Transaction>> getAllTransactions(
            @PageableDefault(size = 5, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<Transaction> transactions = transactionService.getAllTransactionsForUser(userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        Transaction transaction = transactionService.getTransactionById(id, userId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN tạo Transaction
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionRequest transactionRequest) {
        Long userId = getCurrentUserId();
        Transaction transaction = transactionService.createTransaction(transactionRequest, userId);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN cập nhật Transaction
    public ResponseEntity<Transaction> updateTransaction(@PathVariable("id") Long id, @Valid @RequestBody TransactionRequest transactionRequest) {
        Long userId = getCurrentUserId();
        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionRequest, userId);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN xóa Transaction của mình
    public ResponseEntity<MessageResponse> deleteTransaction(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.ok(new MessageResponse("Transaction deleted successfully!"));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found by username: " + username));
        return user.getId();
    }
}