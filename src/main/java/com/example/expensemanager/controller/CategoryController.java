package com.example.expensemanager.controller;

import com.example.expensemanager.model.Category;
import com.example.expensemanager.model.User;
import com.example.expensemanager.payload.request.CategoryRequest;
import com.example.expensemanager.payload.response.MessageResponse;
import com.example.expensemanager.repository.UserRepository;
import com.example.expensemanager.service.interfaces.ICategoryService;
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
@RequestMapping("/api/categories")
public class CategoryController {

    private final ICategoryService categoryService;
    private final UserRepository userRepository;

    @Autowired
    public CategoryController(ICategoryService categoryService, UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Category>> getAllCategories() {
        Long userId = getCurrentUserId();
        List<Category> categories = categoryService.getAllCategoriesForUser(userId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        Category category = categoryService.getCategoryById(id, userId);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được tạo category
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        Long userId = getCurrentUserId();
        Category category = categoryService.createCategory(categoryRequest, userId);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được cập nhật category
    public ResponseEntity<Category> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        Long userId = getCurrentUserId();
        Category updatedCategory = categoryService.updateCategory(id, categoryRequest, userId);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được xóa category
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable("id") Long id) {
        Long userId = getCurrentUserId();
        categoryService.deleteCategory(id, userId);
        return ResponseEntity.ok(new MessageResponse("Category deleted successfully!"));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found by username: " + username));
        return user.getId();
    }
}