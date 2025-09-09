package com.istad.docuhub.feature.comment.repository;

import com.istad.docuhub.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Optional<Comment> findByUuid(String uuid);
    boolean existsByUser_Uuid(String userUuid);
    void deleteByUser_Uuid(String userUuid);


}
