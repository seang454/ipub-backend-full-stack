package com.istad.docuhub.feature.category;

import com.istad.docuhub.domain.Category;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsById(int id);
    Optional<Category> findByName(String name);
    @NotNull
    @Override
    Page<Category> findAll(@NotNull Pageable pageable);
    Optional<Category> findByUuid(String uuid);
}
