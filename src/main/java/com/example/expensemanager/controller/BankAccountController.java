package com.example.expensemanager.controller;

import com.example.expensemanager.model.BankAccount;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.BankAccountRequest;
import com.example.expensemanager.payload.response.MessageResponse;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.IBankAccountService;
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
@RequestMapping("/api/bankaccounts")
public class BankAccountController {

    private final IBankAccountService bankAccountService;
    private final UserRepository userRepository;

    @Autowired
    public BankAccountController(IBankAccountService bankAccountService, UserRepository userRepository) {
        this.bankAccountService = bankAccountService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<BankAccount>> getAllBankAccounts() {
        Long userId = getCurrentUserId();
        List<BankAccount> bankAccounts = bankAccountService.getAllBankAccountsForUser(userId);
        return ResponseEntity.ok(bankAccounts);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BankAccount> getBankAccountById(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        BankAccount bankAccount = bankAccountService.getBankAccountById(id, userId);
        return ResponseEntity.ok(bankAccount);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN tạo BankAccount
    public ResponseEntity<BankAccount> createBankAccount(@Valid @RequestBody BankAccountRequest bankAccountRequest) {
        Long userId = getCurrentUserId();
        BankAccount bankAccount = bankAccountService.createBankAccount(bankAccountRequest, userId);
        return new ResponseEntity<>(bankAccount, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN cập nhật BankAccount
    public ResponseEntity<BankAccount> updateBankAccount(@PathVariable("id") Long id, @Valid @RequestBody BankAccountRequest bankAccountRequest) {
        Long userId = getCurrentUserId();
        BankAccount updatedBankAccount = bankAccountService.updateBankAccount(id, bankAccountRequest, userId);
        return ResponseEntity.ok(updatedBankAccount);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN xóa BankAccount của mình
    public ResponseEntity<MessageResponse> deleteBankAccount(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        bankAccountService.deleteBankAccount(id, userId);
        return ResponseEntity.ok(new MessageResponse("Bank Account deleted successfully!"));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found by username: " + username));
        return user.getId();
    }
}