package com.istad.docuhub.feature.user;

import com.istad.docuhub.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@RequestMapping
public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findByIsDeletedFalse(Pageable pageable);
    Optional<User> findByUuidAndIsDeletedFalse(String uuid);
    Optional<User> getUserByUuidAndIsStudentIsFalseAndIsDeletedIsFalse(String uuid);
    Optional<User> getUserByUuidAndIsAdvisorIsFalseAndIsDeletedIsFalse(String uuid);
    Boolean existsByUuidAndIsAdvisorIsFalseAndIsDeletedIsFalse(String uuid);
    Boolean existsByUuidAndIsStudentIsFalseAndIsDeletedIsFalse(String uuid);
    boolean existsByUuidAndIsDeletedFalse(String uuid);
    Boolean existsByIdAndIsDeletedFalse(Integer id);
    List<User> getAllUsersByIsDeletedFalse();
    List<User> findBySlugContainingAndIsDeletedFalse(String slug);
    Optional<User> findByUuid(String uuid);
    List<User> getUserByIsUserTrueAndIsAdvisorFalseAndIsStudentFalseAndIsAdminFalseAndIsDeletedFalse();
    List<User> getUserByIsUserTrueAndIsAdvisorFalseAndIsStudentTrueAndIsAdminFalseAndIsDeletedFalse();
    List<User> getUserByIsUserTrueAndIsAdvisorTrueAndIsStudentFalseAndIsAdminFalseAndIsDeletedFalse();
}
