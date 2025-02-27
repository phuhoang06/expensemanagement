package com.example.expensemanager.service.budget;

import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.model.Budget;
import com.example.expensemanager.model.Category;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.BudgetRequest;
import com.example.expensemanager.payload.response.BudgetResponse;
import com.example.expensemanager.repository.BudgetRepository;
import com.example.expensemanager.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService implements IBudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BudgetResponse> getAllBudgetsByUserId(Long userId, Pageable pageable) {
        Page<Budget> budgets = budgetRepository.findByUserId(userId, pageable);
        return budgets.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgetsByUserId(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    @Override
    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetRequest budgetRequest) {
        Budget budget = new Budget();
        budget.setAmount(budgetRequest.getAmount());
        budget.setStartDate(budgetRequest.getStartDate());
        budget.setEndDate(budgetRequest.getEndDate());
        budget.setDescription(budgetRequest.getDescription());

        // Set User
        budget.setUser(new User(userId));

        // Get Category and handle exception
        Category category = categoryRepository.findByIdAndUserId(budgetRequest.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to user"));
        budget.setCategory(category);

        Budget savedBudget = budgetRepository.save(budget);
        return convertToResponse(savedBudget);
    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest budgetRequest) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found or does not belong to user"));

        budget.setAmount(budgetRequest.getAmount());
        budget.setStartDate(budgetRequest.getStartDate());
        budget.setEndDate(budgetRequest.getEndDate());
        budget.setDescription(budgetRequest.getDescription());

          // Update Category
        Category category = categoryRepository.findByIdAndUserId(budgetRequest.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to user"));
        budget.setCategory(category);

        Budget updatedBudget = budgetRepository.save(budget);
        return convertToResponse(updatedBudget);
    }

    @Override
    @Transactional
    public boolean deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
               .orElseThrow(() -> new ResourceNotFoundException("Budget not found or does not belong to user"));
        budgetRepository.delete(budget);
        return true;
    }
    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long userId, Long budgetId) {
         Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found or does not belong to user"));
        return convertToResponse(budget);
    }

    private BudgetResponse convertToResponse(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getAmount(),
                budget.getStartDate(),
                budget.getEndDate(),
                budget.getDescription(),
                budget.getCategory().getId(),
                budget.getCategory().getName()
        );
    }
}