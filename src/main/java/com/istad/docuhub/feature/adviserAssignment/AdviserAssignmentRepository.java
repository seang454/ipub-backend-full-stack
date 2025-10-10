package com.istad.docuhub.feature.adviserAssignment;

import com.istad.docuhub.domain.AdviserAssignment;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdviserAssignmentRepository extends JpaRepository<AdviserAssignment, Integer> {
    Optional<AdviserAssignment> findByUuid(String uuid); // for lookup by uuid
    Optional<AdviserAssignment> findByPaperUuid(String paperUuid); //check if paper already assigned
    // fetch all assignments by adviser
    List<AdviserAssignment>findByAdvisorUuid(String adviserUuid);
    Page<AdviserAssignment> findByAdvisorUuid(String advisorUuid, Pageable pageable);
    List<AdviserAssignment> findByPaper_UuidIn(List<String> paperUuid);

    // by thong get assignment return paper, student and assign metadata

    @Query("""
    SELECT a FROM AdviserAssignment a
    JOIN a.paper p
    JOIN p.author s
    WHERE a.advisor.uuid = :advisorUuid
    """)
    Page<AdviserAssignment> findAssignmentsByAdvisorUuid(
            @Param("advisorUuid") String advisorUuid,
            Pageable pageable
    );
}
