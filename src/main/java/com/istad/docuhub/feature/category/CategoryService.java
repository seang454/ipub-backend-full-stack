package com.istad.docuhub.feature.category;

import com.istad.docuhub.feature.category.dto.CategoryRequest;
import com.istad.docuhub.feature.category.dto.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CategoryService {
    void createCategory(CategoryRequest request);
    Page<CategoryResponse> getAllCategory(Pageable pageable);
    void updateCategory(String uuid, CategoryRequest request);
    void deleteCategory(String uuid);
    Page<CategoryResponse> searchCategoryBySlug(String slug, Pageable pageable);
}
