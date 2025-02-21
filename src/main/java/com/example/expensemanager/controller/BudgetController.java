package com.example.expensemanager.controller;

import com.example.expensemanager.model.Budget;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.BudgetRequest;
import com.example.expensemanager.payload.response.MessageResponse;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.IBudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final IBudgetService budgetService;
    private final UserRepository userRepository;

    @Autowired
    public BudgetController(IBudgetService budgetService, UserRepository userRepository) {
        this.budgetService = budgetService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Budget>> getAllBudgets() {
        Long userId = getCurrentUserId();
        List<Budget> budgets = budgetService.getAllBudgetsForUser(userId);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Budget> getBudgetById(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        Budget budget = budgetService.getBudgetById(id, userId);
        return ResponseEntity.ok(budget);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN tạo Budget
    public ResponseEntity<Budget> createBudget(@Valid @RequestBody BudgetRequest budgetRequest) {
        Long userId = getCurrentUserId();
        Budget budget = budgetService.createBudget(budgetRequest, userId);
        return new ResponseEntity<>(budget, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN cập nhật Budget
    public ResponseEntity<Budget> updateBudget(@PathVariable("id") Long id, @Valid @RequestBody BudgetRequest budgetRequest) {
        Long userId = getCurrentUserId();
        Budget updatedBudget = budgetService.updateBudget(id, budgetRequest, userId);
        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Cho phép USER và ADMIN xóa Budget của mình
    public ResponseEntity<MessageResponse> deleteBudget(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        budgetService.deleteBudget(id, userId);
        return ResponseEntity.ok(new MessageResponse("Budget deleted successfully!"));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found by username: " + username));
        return user.getId();
    }
}