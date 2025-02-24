package com.example.expensemanager.service.interfaces;

import com.example.expensemanager.payload.request.CategoryRequest;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategoriesForUser(Long userId);
    Category getCategoryById(Long id, Long userId);
    Category createCategory(CategoryRequest categoryRequest, Long userId);
    Category updateCategory(Long id, CategoryRequest categoryRequest, Long userId);
    void deleteCategory(Long id, Long userId);
    // Các phương thức service khác liên quan đến Category
}