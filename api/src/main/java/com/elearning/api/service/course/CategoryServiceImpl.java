package com.elearning.api.service.course;

import com.elearning.api.payload.course.CategoryRequest;
import com.elearning.api.payload.course.CourseCategoryResponse;
import com.elearning.common.domain.course.CourseCategory;
import com.elearning.common.domain.course.CourseCategoryRepository;
import com.elearning.common.enums.Status;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CourseCategoryRepository categoryRepository;

    @Override
    @Transactional
    public Object createCategory(CategoryRequest request) {
        // Check if category with same name already exists
        if (categoryRepository.findByNameAndStatus(request.getName(), Status.NORMAL).isPresent()) {
            throw new BusinessException(StatusCode.CATEGORY_ALREADY_EXISTS);
        }

        CourseCategory category = CourseCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(Status.NORMAL)
                .build();

        category = categoryRepository.save(category);

        return CourseCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getAllCategories() {
        List<CourseCategory> categories = categoryRepository.findByStatus(Status.NORMAL);
        
        List<CourseCategoryResponse> categoryResponses = categories.stream()
                .map(category -> CourseCategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categoryResponses);
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getCategoryById(Long categoryId) {
        CourseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_CATEGORY_NOT_FOUND));

        return CourseCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    @Transactional
    public Object updateCategory(Long categoryId, CategoryRequest request) {
        CourseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_CATEGORY_NOT_FOUND));

        // Check if another category with same name exists (excluding current category)
        categoryRepository.findByNameAndStatus(request.getName(), Status.NORMAL)
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(categoryId)) {
                        throw new BusinessException(StatusCode.CATEGORY_ALREADY_EXISTS);
                    }
                });

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        category = categoryRepository.save(category);

        return CourseCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    @Transactional
    public Object deleteCategory(Long categoryId) {
        CourseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_CATEGORY_NOT_FOUND));

        // Soft delete by setting status to DISABLE
        category.setStatus(Status.DISABLE);
        categoryRepository.save(category);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category deleted successfully");
        response.put("categoryId", categoryId);
        
        return response;
    }
}

