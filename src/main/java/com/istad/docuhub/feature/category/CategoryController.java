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

    @GetMapping
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,   // page index (0 = first page)
            @RequestParam(defaultValue = "10") int size,  // default 10 items per page
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
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
    public List<CategoryResponse> getAllCategories(@RequestParam String slug){
        return categoryService.searchCategoryBySlug(slug);
    }
}
