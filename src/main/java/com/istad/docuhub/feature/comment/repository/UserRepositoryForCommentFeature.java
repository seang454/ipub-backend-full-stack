package com.istad.docuhub.feature.comment.repository;

import com.istad.docuhub.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositoryForCommentFeature extends JpaRepository<User, Integer> {

    @Override
    Optional<User> findById(Integer integer);



}
