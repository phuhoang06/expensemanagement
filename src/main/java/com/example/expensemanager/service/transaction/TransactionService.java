package com.example.expensemanager.service.transaction;

import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.model.BankAccount;
import com.example.expensemanager.model.Category;
import com.example.expensemanager.model.Transaction;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.TransactionRequest;
import com.example.expensemanager.payload.response.TransactionResponse;
import com.example.expensemanager.repository.BankAccountRepository;
import com.example.expensemanager.repository.CategoryRepository;
import com.example.expensemanager.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactionsByUserId(Long userId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserId(userId, pageable);
        return transactions.map(this::convertToResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDate(transactionRequest.getDate());

        // Set User
        transaction.setUser(new User(userId)); // Sử dụng constructor

        // Get Category and handle exception
        Category category = categoryRepository.findByIdAndUserId(transactionRequest.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or not belong to user"));
        transaction.setCategory(category);

        // Get BankAccount (if provided) and handle exception
        if (transactionRequest.getBankAccountId() != null) {
            BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(transactionRequest.getBankAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bank Account not found or not belong to user"));
            transaction.setBankAccount(bankAccount);

            // Update BankAccount balance
            if (transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                bankAccount.setBalance(bankAccount.getBalance().add(transaction.getAmount()));
            } else {
                bankAccount.setBalance(bankAccount.getBalance().subtract(transaction.getAmount().abs()));
            }
            bankAccountRepository.save(bankAccount);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToResponse(savedTransaction);
    }
    @Override
    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionRequest transactionRequest) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or does not belong to user"));

        // Update fields
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDate(transactionRequest.getDate());

        // Update Category
        Category category = categoryRepository.findByIdAndUserId(transactionRequest.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to user"));
        transaction.setCategory(category);


        // Update BankAccount (handle changes)
        // Case 1:  No previous bank account,  new bank account provided.
        if(transaction.getBankAccount() == null && transactionRequest.getBankAccountId() != null)
        {
            BankAccount newBankAccount = bankAccountRepository.findByIdAndUserId(transactionRequest.getBankAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("New Bank Account not found or does not belong to user"));
            transaction.setBankAccount(newBankAccount);

            // Adjust balance of the NEW bank account.
            if (transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                newBankAccount.setBalance(newBankAccount.getBalance().add(transaction.getAmount()));
            }else {
                newBankAccount.setBalance(newBankAccount.getBalance().subtract(transaction.getAmount().abs()));
            }
            bankAccountRepository.save(newBankAccount);
        }
        // Case 2: Previous bank account, now no bank account.
        else if(transaction.getBankAccount() != null && transactionRequest.getBankAccountId() == null)
        {
            // Adjust balance of the OLD bank account
            BankAccount oldBankAccount = transaction.getBankAccount(); // We already know it exists and belongs to the user

            if (transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                oldBankAccount.setBalance(oldBankAccount.getBalance().subtract(transaction.getAmount()));
            }else {
                oldBankAccount.setBalance(oldBankAccount.getBalance().add(transaction.getAmount().abs()));
            }

            bankAccountRepository.save(oldBankAccount);
            transaction.setBankAccount(null); // Remove the association
        }
        // Case 3:  Previous bank account, different new bank account
        else if(transaction.getBankAccount() != null && transactionRequest.getBankAccountId() != null &&
                !transaction.getBankAccount().getId().equals(transactionRequest.getBankAccountId()))
        {
            // Adjust balance of the OLD bank account
            BankAccount oldBankAccount = transaction.getBankAccount();
            if (transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                oldBankAccount.setBalance(oldBankAccount.getBalance().subtract(transaction.getAmount()));
            }else {
                oldBankAccount.setBalance(oldBankAccount.getBalance().add(transaction.getAmount().abs()));
            }
            bankAccountRepository.save(oldBankAccount);

            // Adjust balance of the NEW bank account
            BankAccount newBankAccount = bankAccountRepository.findByIdAndUserId(transactionRequest.getBankAccountId(), userId)
                    .orElseThrow(()-> new ResourceNotFoundException("New Bank Account not found or does not belong to user"));
            transaction.setBankAccount(newBankAccount);

            if (transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                newBankAccount.setBalance(newBankAccount.getBalance().add(transaction.getAmount()));
            }else {
                newBankAccount.setBalance(newBankAccount.getBalance().subtract(transaction.getAmount().abs()));
            }

            bankAccountRepository.save(newBankAccount);

        }
        // Case 4: Same bank account, but amount changed.  MOST COMPLICATED
        else if(transaction.getBankAccount() != null && transactionRequest.getBankAccountId() != null &&
                transaction.getBankAccount().getId().equals(transactionRequest.getBankAccountId())) {

            BankAccount sameBankAccount = transaction.getBankAccount();
            BigDecimal originalAmount = transaction.getAmount();
            BigDecimal newAmount = transactionRequest.getAmount();
            // Calculate the difference
            BigDecimal amountDifference = newAmount.subtract(originalAmount);

            // Update the bank account balance by the *difference*
            sameBankAccount.setBalance(sameBankAccount.getBalance().add(amountDifference));
            bankAccountRepository.save(sameBankAccount);

        }
        // Save and return
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToResponse(updatedTransaction);
    }


    @Override
    @Transactional
    public boolean deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or does not belong to user"));
        // Adjust bank account balance if necessary
        if (transaction.getBankAccount() != null) {
            BankAccount bankAccount = transaction.getBankAccount();
            if (transaction.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                bankAccount.setBalance(bankAccount.getBalance().subtract(transaction.getAmount()));
            } else {
                bankAccount.setBalance(bankAccount.getBalance().add(transaction.getAmount().abs()));
            }
            bankAccountRepository.save(bankAccount);
        }
        transactionRepository.delete(transaction);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or does not belong to user"));
        return convertToResponse(transaction);
    }



    // Helper method to convert Transaction entity to TransactionResponse DTO
    private TransactionResponse convertToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setDescription(transaction.getDescription());
        response.setAmount(transaction.getAmount());
        response.setDate(transaction.getDate());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        response.setCategoryId(transaction.getCategory().getId());
        response.setCategoryName(transaction.getCategory().getName());

        if (transaction.getBankAccount() != null) {
            response.setBankAccountId(transaction.getBankAccount().getId());
            response.setBankAccountName(transaction.getBankAccount().getAccountName());
        }

        return response;
    }
}