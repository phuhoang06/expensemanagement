package com.example.expensemanager.service.budget;

import com.example.expensemanager.payload.request.BudgetRequest;
import com.example.expensemanager.payload.response.BudgetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBudgetService {
    Page<BudgetResponse> getAllBudgetsByUserId(Long userId, Pageable pageable);
    List<BudgetResponse> getAllBudgetsByUserId(Long userId);
    BudgetResponse createBudget(Long userId, BudgetRequest budgetRequest);
    BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest budgetRequest);
    boolean deleteBudget(Long userId, Long budgetId);
    BudgetResponse getBudgetById(Long userId, Long budgetId);
}