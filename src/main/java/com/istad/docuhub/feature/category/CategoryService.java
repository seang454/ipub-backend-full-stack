package com.istad.docuhub.feature.category;

import com.istad.docuhub.feature.category.dto.CategoryRequest;
import com.istad.docuhub.feature.category.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    void createCategory(CategoryRequest request);
    List<CategoryResponse> getAllCategory();
}
