package com.istad.docuhub.Repository;

import com.istad.docuhub.domain.Star;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StarRepository extends JpaRepository<Star, Integer> {

    // Find star by user ID and paper ID
    Optional<Star> findByUserIdAndPaperId(Integer userId, Integer paperId);

    // Check if star exists by user ID and paper ID
    boolean existsByUserIdAndPaperId(Integer userId, Integer paperId);

    // Count stars by paper ID
    Long countByPaperId(Integer paperId);

    // Delete star by user ID and paper ID
    void deleteByUserIdAndPaperId(Integer userId, Integer paperId);

    // Find stars by user ID with pagination
    Page<Star> findByUserIdOrderByStaredAtDesc(Integer userId, Pageable pageable);

    // Find stars by paper ID with pagination
    Page<Star> findByPaperIdOrderByStaredAtDesc(Integer paperId, Pageable pageable);

}
