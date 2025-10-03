package com.istad.docuhub.feature.star;

import com.istad.docuhub.domain.Star;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StarRepository extends JpaRepository<Star, Integer> {

    // Find all stars by paper UUID
    List<Star> findByPaper_Uuid(String paperUuid);

    // Count stars by paper UUID
    long countByPaper_Uuid(String paperUuid);

    // Find a star by paper UUID and user UUID (for unstar check)
    Optional<Star> findByPaper_UuidAndUser_Uuid(String paperUuid, String userUuid);

    // Check if user already starred a paper
    boolean existsByPaper_UuidAndUser_Uuid(String paperUuid, String userUuid);

    // Delete a star by paper UUID and user UUID
    void deleteByPaper_UuidAndUser_Uuid(String paperUuid, String userUuid);

    boolean existsByUuid(String uuid);

    List<Star> findStarByPaper_UuidIn(List<String> paperUuids);
}
