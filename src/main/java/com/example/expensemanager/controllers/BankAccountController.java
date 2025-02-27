package com.example.expensemanager.controllers;

import com.example.expensemanager.payload.request.BankAccountRequest;
import com.example.expensemanager.payload.response.BankAccountResponse;
import com.example.expensemanager.service.bankaccount.IBankAccountService;
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
@RequestMapping("/api/users/{userId}/bank-accounts") // Corrected path
public class BankAccountController { // Corrected class name

    @Autowired
    private IBankAccountService bankAccountService; // Corrected variable name

    @GetMapping
    public ResponseEntity<Page<BankAccountResponse>> getAllBankAccountsByUserId(
            @PathVariable Long userId,
            @PageableDefault(sort = "accountName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<BankAccountResponse> bankAccounts = bankAccountService.getAllBankAccountsByUserId(userId, pageable);
        return ResponseEntity.ok(bankAccounts);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BankAccountResponse>> getAllBankAccounts(@PathVariable Long userId) {
        List<BankAccountResponse> bankAccounts = bankAccountService.getAllBankAccountsByUserId(userId);
        return ResponseEntity.ok(bankAccounts);
    }
    @GetMapping("/{bankAccountId}") // Corrected path variable name
    public ResponseEntity<BankAccountResponse> getBankAccountById(
            @PathVariable Long userId,
            @PathVariable Long bankAccountId) { // Corrected path variable name
        BankAccountResponse bankAccount = bankAccountService.getBankAccountById(userId, bankAccountId);
        return ResponseEntity.ok(bankAccount);
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createBankAccount(
            @PathVariable Long userId,
            @Valid @RequestBody BankAccountRequest bankAccountRequest) {
        BankAccountResponse createdBankAccount = bankAccountService.createBankAccount(userId, bankAccountRequest);
        return new ResponseEntity<>(createdBankAccount, HttpStatus.CREATED);
    }

    @PutMapping("/{bankAccountId}") // Corrected path variable name
    public ResponseEntity<BankAccountResponse> updateBankAccount(
            @PathVariable Long userId,
            @PathVariable Long bankAccountId, // Corrected path variable name
            @Valid @RequestBody BankAccountRequest bankAccountRequest) {
        BankAccountResponse updatedBankAccount = bankAccountService.updateBankAccount(userId, bankAccountId, bankAccountRequest);
        return ResponseEntity.ok(updatedBankAccount);
    }

    @DeleteMapping("/{bankAccountId}") // Corrected path variable name
    public ResponseEntity<Void> deleteBankAccount(
            @PathVariable Long userId,
            @PathVariable Long bankAccountId) { // Corrected path variable name
        bankAccountService.deleteBankAccount(userId, bankAccountId);
        return ResponseEntity.noContent().build();
    }
}