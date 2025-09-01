package com.istad.docuhub.feature.paper;

import com.istad.docuhub.domain.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Integer> {
    boolean existsById(Integer id);

    Optional<Paper> findByUuid(String uuid);
}
