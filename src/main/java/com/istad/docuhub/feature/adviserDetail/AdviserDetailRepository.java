package com.istad.docuhub.feature.adviserDetail;

import com.istad.docuhub.domain.AdviserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdviserDetailRepository extends JpaRepository<AdviserDetail, Integer> {
    Optional<AdviserDetail> findByUser_Uuid(String userUuidUuid);

}
