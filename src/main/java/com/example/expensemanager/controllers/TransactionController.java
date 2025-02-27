package com.example.expensemanager.controllers;

import com.example.expensemanager.payload.request.TransactionRequest;
import com.example.expensemanager.payload.response.TransactionResponse;
import com.example.expensemanager.service.transaction.ITransactionService;
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
@RequestMapping("/api/users/{userId}/transactions")
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactionsByUserId(
            @PathVariable Long userId,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TransactionResponse> transactions = transactionService.getAllTransactionsByUserId(userId, pageable);
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/all") // Dành cho trường hợp muốn lấy tất cả, không phân trang
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(@PathVariable Long userId){
        List<TransactionResponse> transactions = transactionService.getAllTransactionsByUserId(userId);
        return  ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        TransactionResponse transaction = transactionService.getTransactionById(userId, transactionId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable Long userId,
            @Valid @RequestBody TransactionRequest transactionRequest) {
        TransactionResponse createdTransaction = transactionService.createTransaction(userId, transactionRequest);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionRequest transactionRequest) {
        TransactionResponse updatedTransaction = transactionService.updateTransaction(userId, transactionId, transactionRequest);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }
}