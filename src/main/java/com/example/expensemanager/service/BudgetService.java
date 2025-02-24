package com.example.expensemanager.service;

import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.payload.request.BudgetRequest;
import com.example.expensemanager.repository.BudgetRepository;
import com.example.expensemanager.repository.CategoryRepository;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.IBudgetService; // Import đúng interface từ 'interfaces'
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BudgetService implements IBudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository; // Cần để validate user tồn tại

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Budget> getAllBudgetsForUser(Long userId) {
        validateUserExists(userId);
        return budgetRepository.findByUserId(userId);
    }

    @Override
    public Budget getBudgetById(Long id, Long userId) {
        validateUserExists(userId);
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
    }

    @Override
    @Transactional
    public Budget createBudget(BudgetRequest budgetRequest, Long userId) {
        validateUserExists(userId);
        Category category = categoryRepository.findById(budgetRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + budgetRequest.getCategoryId()));

        Budget budget = new Budget();
        budget.setAmount(budgetRequest.getAmount());
        budget.setStartDate(budgetRequest.getStartDate());
        budget.setEndDate(budgetRequest.getEndDate());
        budget.setCategory(category);
        budget.setUser(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
        return budgetRepository.save(budget);
    }

    @Override
    @Transactional
    public Budget updateBudget(Long id, BudgetRequest budgetRequest, Long userId) {
        validateUserExists(userId);
        Budget existingBudget = getBudgetById(id, userId); // Kiểm tra budget tồn tại và thuộc về user

        Category category = categoryRepository.findById(budgetRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + budgetRequest.getCategoryId()));

        existingBudget.setAmount(budgetRequest.getAmount());
        existingBudget.setStartDate(budgetRequest.getStartDate());
        existingBudget.setEndDate(budgetRequest.getEndDate());
        existingBudget.setCategory(category);

        return budgetRepository.save(existingBudget);
    }

    @Override
    @Transactional
    public void deleteBudget(Long id, Long userId) {
        validateUserExists(userId);
        Budget budget = getBudgetById(id, userId); // Kiểm tra budget tồn tại và thuộc về user
        budgetRepository.delete(budget);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }
}