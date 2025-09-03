package com.istad.docuhub.feature.studentDetail.repository;

import com.istad.docuhub.domain.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepositoryForStudentDetailFeature extends JpaRepository<StudentDetail, Integer> {

    Optional<StudentDetail> findByUuid(String uuid);


    boolean existsByUuid(String uuid);


    void deleteByUuid(String uuid);


}
