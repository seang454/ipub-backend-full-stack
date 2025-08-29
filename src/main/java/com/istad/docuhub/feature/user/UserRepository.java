package com.istad.docuhub.feature.user;

import com.istad.docuhub.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean findByUuidAndIsDeletedFalse(String uuid);
    Boolean existsByIdAndIsDeletedFalse(Integer id);
    List<User> getAllUsersByIsDeletedFalse();
    List<User> findBySlugContainingAndIsDeletedFalse(String slug);
}
