package com.istad.docuhub.Repository;

import com.istad.docuhub.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RequestMapping
public interface UserRepository extends JpaRepository<User, Integer> {


    /* --- existing id-based helpers --- */
    Optional<User> findUserById(Integer id);
    User getUserByIdAndIsDeletedFalse(Integer id);
    Boolean existsByIdAndIsDeletedFalse(Integer id);

    /* --- new uuid-based helpers --- */
    Optional<User> findByUuid(String uuid);
    boolean existsByUuid(String uuid);


}
