package com.example.expensemanager.service.category;

import com.example.expensemanager.model.Category;
import com.example.expensemanager.payload.request.CategoryRequest;
import com.example.expensemanager.payload.response.CategoryResponse;
import java.util.List;

public interface ICategoryService {
    List<CategoryResponse> getAllCategoriesByUserId(Long userId);
    CategoryResponse createCategory(Long userId, CategoryRequest categoryRequest);
    CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest categoryRequest);
    boolean deleteCategory(Long userId, Long categoryId);
    CategoryResponse getCategoryById(Long userId, Long categoryId);
}