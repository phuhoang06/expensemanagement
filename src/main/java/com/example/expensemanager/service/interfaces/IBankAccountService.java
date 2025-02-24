package com.example.expensemanager.service.interfaces;

import com.example.expensemanager.payload.request.BankAccountRequest;

import java.util.List;

public interface IBankAccountService {
    List<BankAccount> getAllBankAccountsForUser(Long userId);
    BankAccount getBankAccountById(Long id, Long userId);
    BankAccount createBankAccount(BankAccountRequest bankAccountRequest, Long userId);
    BankAccount updateBankAccount(Long id, BankAccountRequest bankAccountRequest, Long userId);
    void deleteBankAccount(Long id, Long userId);
    // Các phương thức service khác liên quan đến BankAccount
}