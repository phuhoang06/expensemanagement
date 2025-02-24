package com.example.expensemanager.service.interfaces;

import com.example.expensemanager.payload.request.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITransactionService {
    Page<Transaction> getAllTransactionsForUser(Long userId, Pageable pageable);
    Transaction getTransactionById(Long id, Long userId);
    Transaction createTransaction(TransactionRequest transactionRequest, Long userId);
    Transaction updateTransaction(Long id, TransactionRequest transactionRequest, Long userId);
    void deleteTransaction(Long id, Long userId);
    // Các phương thức service khác liên quan đến Transaction (ví dụ: thống kê, báo cáo...)
}