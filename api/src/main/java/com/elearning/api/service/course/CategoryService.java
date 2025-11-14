package com.elearning.api.service.course;

import com.elearning.api.payload.course.CategoryRequest;

public interface CategoryService {
    Object createCategory(CategoryRequest request);
    Object getAllCategories();
    Object getCategoryById(Long categoryId);
    Object updateCategory(Long categoryId, CategoryRequest request);
    Object deleteCategory(Long categoryId);
}

