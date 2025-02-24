package com.example.expensemanager.service;

import com.example.expensemanager.exception.ResourceNotFoundException;
import com.example.expensemanager.payload.request.CategoryRequest;
import com.example.expensemanager.repository.CategoryRepository;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.ICategoryService; // Import đúng interface từ 'interfaces'
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository; // Cần để validate user tồn tại

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Category> getAllCategoriesForUser(Long userId) {
        validateUserExists(userId);
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public Category getCategoryById(Long id, Long userId) {
        validateUserExists(userId);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional
    public Category createCategory(CategoryRequest categoryRequest, Long userId) {
        validateUserExists(userId);
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setUser(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryRequest categoryRequest, Long userId) {
        validateUserExists(userId);
        Category existingCategory = getCategoryById(id, userId); // Kiểm tra category tồn tại và thuộc về user

        existingCategory.setName(categoryRequest.getName());
        existingCategory.setDescription(categoryRequest.getDescription());

        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id, Long userId) {
        validateUserExists(userId);
        Category category = getCategoryById(id, userId); // Kiểm tra category tồn tại và thuộc về user
        categoryRepository.delete(category);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }
}