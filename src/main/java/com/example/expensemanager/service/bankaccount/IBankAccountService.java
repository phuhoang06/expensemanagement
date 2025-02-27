package com.example.expensemanager.service.bankaccount;

import com.example.expensemanager.payload.request.BankAccountRequest;
import com.example.expensemanager.payload.response.BankAccountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IBankAccountService {
    Page<BankAccountResponse> getAllBankAccountsByUserId(Long userId, Pageable pageable);
    List<BankAccountResponse> getAllBankAccountsByUserId(Long userId);
    BankAccountResponse createBankAccount(Long userId, BankAccountRequest bankAccountRequest);
    BankAccountResponse updateBankAccount(Long userId, Long bankAccountId, BankAccountRequest bankAccountRequest);
    boolean deleteBankAccount(Long userId, Long bankAccountId);
    BankAccountResponse getBankAccountById(Long userId, Long bankAccountId);
}