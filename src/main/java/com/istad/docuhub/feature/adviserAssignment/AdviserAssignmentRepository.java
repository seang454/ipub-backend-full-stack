package com.istad.docuhub.feature.adviserAssignment;

import com.istad.docuhub.domain.AdviserAssignment;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdviserAssignmentRepository extends JpaRepository<AdviserAssignment, Integer> {
    Optional<AdviserAssignment> findByUuid(String uuid); // for lookup by uuid
    Optional<AdviserAssignment> findByPaperUuid(String paperUuid); //check if paper already assigned
    // fetch all assignments by adviser
    List<AdviserAssignment>findByAdvisorUuid(String adviserUuid);
    Page<AdviserAssignment> findByAdvisorUuid(String advisorUuid, Pageable pageable);

    List<AdviserAssignment> findByPaper_UuidIn(List<String> paperUuid);
}
