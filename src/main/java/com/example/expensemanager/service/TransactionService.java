package com.example.expensemanager.service;

import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.model.Category;
import com.example.expensemanager.model.Transaction;
import com.example.expensemanager.model.TransactionType;
import com.example.expensemanager.payload.request.TransactionRequest;
import com.example.expensemanager.repository.BankAccountRepository;
import com.example.expensemanager.repository.CategoryRepository;
import com.example.expensemanager.repository.TransactionRepository;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.ITransactionService; // Import đúng interface từ 'interfaces'
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository; // Cần để validate user tồn tại

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<Transaction> getAllTransactionsForUser(Long userId, Pageable pageable) {
        validateUserExists(userId);
        return transactionRepository.findByUserId(userId, pageable);
    }

    @Override
    public Transaction getTransactionById(Long id, Long userId) {
        validateUserExists(userId);
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionRequest transactionRequest, Long userId) {
        validateUserExists(userId);
        Category category = categoryRepository.findById(transactionRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + transactionRequest.getCategoryId()));

        Transaction transaction = new Transaction();
        transaction.setTransactionDate(transactionRequest.getTransactionDate());
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setType(TransactionType.valueOf(transactionRequest.getType())); // Convert String to Enum
        transaction.setCategory(category);
        transaction.setUser(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
        if (transactionRequest.getBankAccountId() != null) {
            bankAccountRepository.findById(transactionRequest.getBankAccountId())
                    .ifPresent(transaction::setBankAccount); // Set BankAccount nếu có và tồn tại
        }

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Long id, TransactionRequest transactionRequest, Long userId) {
        validateUserExists(userId);
        Transaction existingTransaction = getTransactionById(id, userId); // Kiểm tra transaction tồn tại và thuộc về user

        Category category = categoryRepository.findById(transactionRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + transactionRequest.getCategoryId()));

        existingTransaction.setTransactionDate(transactionRequest.getTransactionDate());
        existingTransaction.setDescription(transactionRequest.getDescription());
        existingTransaction.setAmount(transactionRequest.getAmount());
        existingTransaction.setType(TransactionType.valueOf(transactionRequest.getType()));
        existingTransaction.setCategory(category);
        if (transactionRequest.getBankAccountId() != null) {
            bankAccountRepository.findById(transactionRequest.getBankAccountId())
                    .ifPresent(existingTransaction::setBankAccount);
        } else {
            existingTransaction.setBankAccount(null); // Nếu bankAccountId không được gửi lên khi update thì set null
        }

        return transactionRepository.save(existingTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id, Long userId) {
        validateUserExists(userId);
        Transaction transaction = getTransactionById(id, userId); // Kiểm tra transaction tồn tại và thuộc về user
        transactionRepository.delete(transaction);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }
}