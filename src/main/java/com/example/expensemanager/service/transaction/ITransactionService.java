package com.example.expensemanager.service.transaction;

import com.example.expensemanager.model.Transaction;
import com.example.expensemanager.payload.request.TransactionRequest;
import com.example.expensemanager.payload.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ITransactionService {
    Page<TransactionResponse> getAllTransactionsByUserId(Long userId, Pageable pageable);

    TransactionResponse createTransaction(Long userId, TransactionRequest transactionRequest);

    TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionRequest transactionRequest);

    boolean deleteTransaction(Long userId, Long transactionId);

    TransactionResponse getTransactionById(Long userId, Long transactionId); // Thêm method getById

    List<TransactionResponse> getAllTransactionsByUserId(Long userId); // Thêm method get all
}