package com.istad.docuhub.feature.studentDetail;

import com.istad.docuhub.domain.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface StudentDetailRepository extends JpaRepository<StudentDetail, Integer> {
    // thong added method find student detail
    Optional<StudentDetail> findByUser_Uuid(String userUuid);
}
