package com.istad.docuhub.feature.paper;

import com.istad.docuhub.domain.Paper;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Integer> {
    boolean existsById(@NotNull Integer id);

    Optional<Paper> findByUuid(String uuid);

    @NotNull
    @Override
    Page<Paper> findAll(@NotNull Pageable pageable);

    Page<Paper> findByIsDeletedIsFalseAndIsApprovedTrueAndIsPublishedIsTrue(Pageable pageable);
    Page<Paper> findByIsApprovedFalse(Pageable pageable);
    Page<Paper> findByIsDeletedIsFalseAndIsApprovedTrue(Pageable pageable);
    Page<Paper> findByAuthor_UuidAndIsDeletedFalse(String uuid, Pageable pageable);

    Page<Paper> findByAuthor_UuidAndIsDeletedFalseAndIsApprovedTrue(String uuid, Pageable pageable);
    List<Paper> findPaperByAuthor_UuidAndIsDeletedFalseAndIsApprovedTrue(String uuid);

    Optional<Paper> findByUuidAndIsDeletedFalseAndIsApprovedFalse(String uuid);

    // added by thongfazon
    Boolean existsByUuid(String uuid);

    Optional<Paper> findPaperByUuidAndAuthor_Uuid(String uuid, String authorUuid);

    Optional<Paper> findPaperByUuidAndIsApprovedTrueAndIsPublishedTrueAndIsDeletedFalse(String uuid);
}