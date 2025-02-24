package com.example.expensemanager.service.interfaces;

import com.example.expensemanager.payload.request.BudgetRequest;

import java.util.List;

public interface IBudgetService {
    List<Budget> getAllBudgetsForUser(Long userId);
    Budget getBudgetById(Long id, Long userId);
    Budget createBudget(BudgetRequest budgetRequest, Long userId);
    Budget updateBudget(Long id, BudgetRequest budgetRequest, Long userId);
    void deleteBudget(Long id, Long userId);
    // Các phương thức service khác liên quan đến Budget
}