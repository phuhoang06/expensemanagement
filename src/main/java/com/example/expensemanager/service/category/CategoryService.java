package com.example.expensemanager.service.category;

import com.example.expensemanager.exception.BadRequestException;
import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.model.Category;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.CategoryRequest;
import com.example.expensemanager.payload.response.CategoryResponse;
import com.example.expensemanager.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategoriesByUserId(Long userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest categoryRequest) {
        // Check for duplicate category name
        Optional<Category> existingCategory = categoryRepository.findByNameAndUserId(categoryRequest.getName(), userId);
        if (existingCategory.isPresent()) {
            throw new BadRequestException("Category with name '" + categoryRequest.getName() + "' already exists for this user.");
        }
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setUser(new User(userId)); // Set the user

        Category savedCategory = categoryRepository.save(category);
        return convertToResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest categoryRequest) {
        // Check if exist and belong to user
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to this user."));
        // Check for duplicate name (excluding the current category being updated)
        Optional<Category> existingCategory = categoryRepository.findByNameAndUserId(categoryRequest.getName(), userId);
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(categoryId)) {
            throw new BadRequestException("Category with name '" + categoryRequest.getName() + "' already exists for this user.");
        }

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return convertToResponse(updatedCategory);
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to this user."));
        categoryRepository.delete(category);
        return true;
    }
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to user"));
        return convertToResponse(category);
    }

    private CategoryResponse convertToResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}