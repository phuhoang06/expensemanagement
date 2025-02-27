package com.example.expensemanager.controllers;

import com.example.expensemanager.payload.request.BudgetRequest;
import com.example.expensemanager.payload.response.BudgetResponse;
import com.example.expensemanager.service.budget.IBudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/budgets")
public class BudgetController {
    @Autowired
    private IBudgetService budgetService;

    @GetMapping
    public ResponseEntity<Page<BudgetResponse>> getAllBudgetsByUserId(
            @PathVariable Long userId,
            @PageableDefault(sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BudgetResponse> budgets = budgetService.getAllBudgetsByUserId(userId, pageable);
        return ResponseEntity.ok(budgets);
    }
    @GetMapping("/all") // Dành cho trường hợp muốn lấy tất cả, không phân trang
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(@PathVariable Long userId){
        List<BudgetResponse> budgets = budgetService.getAllBudgetsByUserId(userId);
        return  ResponseEntity.ok(budgets);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable Long userId,
            @PathVariable Long budgetId) {
        BudgetResponse budget = budgetService.getBudgetById(userId, budgetId);
        return ResponseEntity.ok(budget);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @PathVariable Long userId,
            @Valid @RequestBody BudgetRequest budgetRequest) {
        BudgetResponse createdBudget = budgetService.createBudget(userId, budgetRequest);
        return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(@PathVariable Long userId, @PathVariable Long budgetId,
            @Valid @RequestBody BudgetRequest budgetRequest) {
        BudgetResponse updatedBudget = budgetService.updateBudget(userId, budgetId, budgetRequest);
        return ResponseEntity.ok(updatedBudget);
    }
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long userId, @PathVariable Long budgetId){
        budgetService.deleteBudget(userId,budgetId);
        return ResponseEntity.noContent().build();
    }
}