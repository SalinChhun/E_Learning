package com.elearning.api.controller;

import com.elearning.api.payload.course.CategoryRequest;
import com.elearning.api.service.course.CategoryService;
import com.elearning.common.common.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wba/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "APIs for managing course categories")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController extends RestApiResponse {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(
            summary = "Create a new category",
            description = "Creates a new course category. Category name must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or category name already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ok(categoryService.createCategory(request));
    }

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Retrieves a list of all active course categories"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getAllCategories() {
        return ok(categoryService.getAllCategories());
    }

    @GetMapping("/{categoryId}")
    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a specific category by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId) {
        return ok(categoryService.getCategoryById(categoryId));
    }

    @PutMapping("/{categoryId}")
    @Operation(
            summary = "Update category",
            description = "Updates an existing category. Category name must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input or category name already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        return ok(categoryService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    @Operation(
            summary = "Delete category",
            description = "Soft deletes a category by setting its status to DISABLE. The category will not appear in GET requests."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId) {
        return ok(categoryService.deleteCategory(categoryId));
    }
}

