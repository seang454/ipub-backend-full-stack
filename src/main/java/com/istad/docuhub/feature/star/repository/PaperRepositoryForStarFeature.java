package com.istad.docuhub.feature.star.repository;

import com.istad.docuhub.domain.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaperRepositoryForStarFeature extends JpaRepository<Paper, Integer> {


    @Override
    Optional<Paper> findById(Integer integer);
}
