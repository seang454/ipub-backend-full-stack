package com.istad.docuhub.feature.category;

import com.istad.docuhub.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsById(int id);
    Optional<Category> findByName(String name);
}
