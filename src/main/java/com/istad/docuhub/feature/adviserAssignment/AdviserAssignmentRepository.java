package com.istad.docuhub.feature.adviserAssignment;

import com.istad.docuhub.domain.AdviserAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdviserAssignmentRepository extends JpaRepository<AdviserAssignment, Integer> {
    Optional<AdviserAssignment> findByUuid(String uuid); // for lookup by uuid
    boolean existsByPaper_Uuid(String paperUuid); //check if paper already assigned
}
