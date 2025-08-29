package com.istad.docuhub.feature.user;

import com.istad.docuhub.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RequestMapping
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUuidAndIsDeletedFalse(String uuid);
    Optional<User> getUserByUuidAndIsStudentIsFalseAndIsDeletedIsFalse(String uuid);
    Optional<User> getUserByUuidAndIsAdvisorIsFalseAndIsDeletedIsFalse(String uuid);
    Boolean existsByUuidAndIsAdvisorIsFalseAndIsDeletedIsFalse(String uuid);
    Boolean existsByUuidAndIsStudentIsFalseAndIsDeletedIsFalse(String uuid);
    boolean existsByUuidAndIsDeletedFalse(String uuid);
    Boolean existsByIdAndIsDeletedFalse(Integer id);
    List<User> getAllUsersByIsDeletedFalse();
    List<User> findBySlugContainingAndIsDeletedFalse(String slug);
    List<User> getUserByIsUserTrueAndIsAdvisorFalseAndIsStudentFalseAndIsAdminFalseAndIsDeletedFalse();
    List<User> getUserByIsUserTrueAndIsAdvisorFalseAndIsStudentTrueAndIsAdminFalseAndIsDeletedFalse();
    List<User> getUserByIsUserTrueAndIsAdvisorTrueAndIsStudentFalseAndIsAdminFalseAndIsDeletedFalse();
}
