package com.istad.docuhub.feature.paper;

import com.istad.docuhub.domain.Paper;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Integer> {
    boolean existsById(@NotNull Integer id);

    Optional<Paper> findByUuid(String uuid);

    List<Paper> findByIsDeletedIsFalseAndIsApprovedTrueAndIsPublishedIsTrue();
    List<Paper> findByIsApprovedFalse();
    List<Paper> findByIsDeletedIsFalseAndIsApprovedTrue();
    List<Paper> findByAuthor_UuidAndIsDeletedFalse(String id);

    List<Paper> findByAuthor_UuidAndIsDeletedFalseAndIsApprovedTrue(String authorUuid);

    Optional<Paper> findByUuidAndIsDeletedFalseAndIsApprovedFalse(String uuid);

    boolean existsByIdAndIsPublishedTrue(Integer id); //added by Vannarith
}