package com.istad.docuhub.feature.studentDetail;

import com.istad.docuhub.domain.StudentDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface StudentDetailRepository extends JpaRepository<StudentDetail, Integer> {
    // thong added method find student detail
    Optional<StudentDetail> findByUser_Uuid(String userUuid);
    // Paginated query for pending students
    @Query("SELECT s FROM StudentDetail s WHERE s.status = com.istad.docuhub.enums.STATUS.PENDING AND s.isStudent = false")
    Page<StudentDetail> findPendingStudents(Pageable pageable);
}
