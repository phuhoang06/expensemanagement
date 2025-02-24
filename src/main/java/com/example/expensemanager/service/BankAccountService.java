package com.example.expensemanager.service;

import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.payload.request.BankAccountRequest;
import com.example.expensemanager.repository.BankAccountRepository;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.IBankAccountService; // Import đúng interface từ 'interfaces'
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BankAccountService implements IBankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository; // Cần để validate user tồn tại

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<BankAccount> getAllBankAccountsForUser(Long userId) {
        validateUserExists(userId);
        return bankAccountRepository.findByUserId(userId);
    }

    @Override
    public BankAccount getBankAccountById(Long id, Long userId) {
        validateUserExists(userId);
        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount not found with id: " + id));
    }

    @Override
    @Transactional
    public BankAccount createBankAccount(BankAccountRequest bankAccountRequest, Long userId) {
        validateUserExists(userId);
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountName(bankAccountRequest.getAccountName());
        bankAccount.setAccountNumber(bankAccountRequest.getAccountNumber());
        bankAccount.setBankName(bankAccountRequest.getBankName());
        bankAccount.setBalance(bankAccountRequest.getBalance());
        bankAccount.setUser(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    @Transactional
    public BankAccount updateBankAccount(Long id, BankAccountRequest bankAccountRequest, Long userId) {
        validateUserExists(userId);
        BankAccount existingBankAccount = getBankAccountById(id, userId); // Kiểm tra bankAccount tồn tại và thuộc về user

        existingBankAccount.setAccountName(bankAccountRequest.getAccountName());
        existingBankAccount.setAccountNumber(bankAccountRequest.getAccountNumber());
        existingBankAccount.setBankName(bankAccountRequest.getBankName());
        existingBankAccount.setBalance(bankAccountRequest.getBalance());

        return bankAccountRepository.save(existingBankAccount);
    }

    @Override
    @Transactional
    public void deleteBankAccount(Long id, Long userId) {
        validateUserExists(userId);
        BankAccount bankAccount = getBankAccountById(id, userId); // Kiểm tra bankAccount tồn tại và thuộc về user
        bankAccountRepository.delete(bankAccount);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }
}