package com.istad.docuhub.feature.specialize;

import com.istad.docuhub.domain.Specialize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecializeRepository extends JpaRepository<Specialize, Integer> {
    Optional<Specialize> findByUuid(String uuid);
    boolean existsBySlug(String slug);
}
