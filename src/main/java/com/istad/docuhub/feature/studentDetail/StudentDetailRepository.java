package com.istad.docuhub.feature.studentDetail;

import com.istad.docuhub.domain.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

interface StudentDetailRepository extends JpaRepository<StudentDetail, Integer> {
}
