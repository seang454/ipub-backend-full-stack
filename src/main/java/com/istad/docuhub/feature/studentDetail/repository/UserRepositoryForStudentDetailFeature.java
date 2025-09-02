package com.istad.docuhub.feature.studentDetail.repository;

import com.istad.docuhub.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositoryForStudentDetailFeature extends JpaRepository<User, Integer> {

    Optional<User> findByUuid(String uuid);

}
