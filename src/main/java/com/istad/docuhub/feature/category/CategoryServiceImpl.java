package com.istad.docuhub.feature.category;

import com.istad.docuhub.domain.Category;
import com.istad.docuhub.feature.category.dto.CategoryRequest;
import com.istad.docuhub.feature.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

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
    public List<CategoryResponse> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategoryResponse(
                        category.getName(),
                        category.getSlug()
                ))
                .toList();
    }
}
