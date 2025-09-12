package com.istad.docuhub.feature.category;

import com.istad.docuhub.domain.Category;
import com.istad.docuhub.feature.category.dto.CategoryRequest;
import com.istad.docuhub.feature.category.dto.CategoryResponse;
import com.istad.docuhub.feature.category.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public void createCategory(CategoryRequest categoryRequest) {
        String slug = categoryRequest.name().toLowerCase().replace(" ", "-");
        int id;
        int retries = 0;
        do {
            if (retries++ > 50) {
                throw new RuntimeException("Unable to generate unique ID after 50 attempts");
            }
            id = new Random().nextInt(Integer.parseInt("1000000"));
        } while (categoryRepository.existsById(id));

        Category category = Category.builder()
                .id(id)
                .name(categoryRequest.name())
                .slug(slug)
                .uuid(UUID.randomUUID().toString())
                .createdDate(LocalDate.now())
                .build();

        categoryRepository.save(category);
    }

    @Override
    public Page<CategoryResponse> getAllCategory(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(category -> new CategoryResponse(
                category.getUuid(),
                category.getName(),
                category.getSlug()
        ));
    }

    @Override
    public void updateCategory(String uuid, CategoryRequest request) {
        Category category = categoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(request.name());
        category.setSlug(request.name().toLowerCase().replace(" ", "-"));
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String uuid) {
        Category category = categoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(category);
    }

    @Override
    public Page<CategoryResponse> searchCategoryBySlug(String slug, Pageable pageable) {
        Page<Category> categories = categoryRepository.findBySlugContaining(slug, pageable);
        return categories.map(categoryMapper::toCategoryResponse);
    }
}
