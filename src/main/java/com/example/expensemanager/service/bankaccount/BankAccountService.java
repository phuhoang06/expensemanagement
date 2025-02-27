package com.example.expensemanager.service.bankaccount;

import com.example.expensemanager.exception.BadRequestException;
import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.model.BankAccount;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.BankAccountRequest;
import com.example.expensemanager.payload.response.BankAccountResponse;
import com.example.expensemanager.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankAccountService implements IBankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccountResponse> getAllBankAccountsByUserId(Long userId, Pageable pageable) {
        Page<BankAccount> bankAccounts = bankAccountRepository.findByUserId(userId, pageable);
        return bankAccounts.map(this::convertToResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getAllBankAccountsByUserId(Long userId){
        List<BankAccount> bankAccounts = bankAccountRepository.findByUserId(userId);
        return bankAccounts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BankAccountResponse createBankAccount(Long userId, BankAccountRequest bankAccountRequest) {
        // Check for duplicate account name
        Optional<BankAccount> existingAccount = bankAccountRepository.findByAccountNameAndUserId(bankAccountRequest.getAccountName(), userId);
        if (existingAccount.isPresent()) {
            throw new BadRequestException("Bank account with name '" + bankAccountRequest.getAccountName() + "' already exists for this user.");
        }
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountName(bankAccountRequest.getAccountName());
        bankAccount.setBalance(bankAccountRequest.getBalance());
        bankAccount.setDescription(bankAccountRequest.getDescription());
        bankAccount.setUser(new User(userId)); // Set the user

        BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
        return convertToResponse(savedBankAccount);
    }

    @Override
    @Transactional
    public BankAccountResponse updateBankAccount(Long userId, Long bankAccountId, BankAccountRequest bankAccountRequest) {
        BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(bankAccountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found or does not belong to user"));

        // Check for duplicate name (excluding the current account being updated)
        Optional<BankAccount> existingAccount = bankAccountRepository.findByAccountNameAndUserId(bankAccountRequest.getAccountName(), userId);
        if (existingAccount.isPresent() && !existingAccount.get().getId().equals(bankAccountId)) {
            throw new BadRequestException("Bank account with name '" + bankAccountRequest.getAccountName() + "' already exists for this user.");
        }

        bankAccount.setAccountName(bankAccountRequest.getAccountName());
        bankAccount.setBalance(bankAccountRequest.getBalance());  // Allow balance updates directly
        bankAccount.setDescription(bankAccountRequest.getDescription());

        BankAccount updatedBankAccount = bankAccountRepository.save(bankAccount);
        return convertToResponse(updatedBankAccount);
    }

    @Override
    @Transactional
    public boolean deleteBankAccount(Long userId, Long bankAccountId) {
        BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(bankAccountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found or does not belong to user"));
        if (!bankAccount.getTransactions().isEmpty()) {
            throw new BadRequestException("Cannot delete bank account with associated transactions.");
        }
        bankAccountRepository.delete(bankAccount);
        return true;
    }
    @Override
    @Transactional(readOnly = true)
    public BankAccountResponse getBankAccountById(Long userId, Long bankAccountId) {
        BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(bankAccountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found or does not belong to user"));;
        return convertToResponse(bankAccount);
    }

    private BankAccountResponse convertToResponse(BankAccount bankAccount) {
        return new BankAccountResponse(
                bankAccount.getId(),
                bankAccount.getAccountName(),
                bankAccount.getBalance(),
                bankAccount.getDescription()
        );
    }
}