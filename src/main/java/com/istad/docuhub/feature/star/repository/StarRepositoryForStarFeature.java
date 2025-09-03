package com.istad.docuhub.feature.star.repository;

import com.istad.docuhub.domain.Star;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StarRepositoryForStarFeature extends JpaRepository<Star, Integer> {

    //Exist by user
    boolean existsByUserId(Integer userId);

    //Exist by paper
    boolean existsByPaperId(Integer paperId);


    void deleteByUserId(Integer userId);


    Long countByPaperId(Integer paperId);

}
