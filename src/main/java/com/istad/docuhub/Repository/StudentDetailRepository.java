package com.istad.docuhub.Repository;

import com.istad.docuhub.domain.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentDetailRepository extends JpaRepository<StudentDetail, Integer> {

    Optional<StudentDetail> findByUuid(String uuid);

    boolean existsByUuid(String uuid);
    void deleteByUuid(String uuid);

}
