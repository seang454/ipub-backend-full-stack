package com.istad.docuhub.feature.category;


import com.istad.docuhub.feature.category.dto.CategoryRequest;
import com.istad.docuhub.feature.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest categoryRequest) {
        categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>(
                Map.of(
                        "message", "Category created successfully"
                ), HttpStatus.CREATED
        );
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,   // 🟢 use an existing field
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CategoryResponse> categories = categoryService.getAllCategory(pageable);
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateCategory(@PathVariable String uuid, @RequestBody CategoryRequest categoryRequest) {
        categoryService.updateCategory(uuid, categoryRequest);
        return new ResponseEntity<>(
                Map.of(
                        "message", "Category updated successfully"
                ), HttpStatus.OK
        );
    }
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteCategory(@PathVariable String uuid) {
        categoryService.deleteCategory(uuid);
        return new ResponseEntity<>(
                Map.of(
                        "message", "Category deleted successfully"
                ), HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("/slug")
    public Page<CategoryResponse> getAllCategories(@RequestParam String slug, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return categoryService.searchCategoryBySlug(slug, PageRequest.of(page, size, Sort.by("name")));
    }
}
