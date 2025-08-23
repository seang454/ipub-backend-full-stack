package com.istad.docuhub.feature.user;

import com.istad.docuhub.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface UserRepository extends JpaRepository<User, Integer> {
    User getUserByIdAndIsDeletedFalse(Integer id);
    Boolean existsByIdAndIsDeletedFalse(Integer id);
}
