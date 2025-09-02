package com.istad.docuhub.feature.comment.repository;

import com.istad.docuhub.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepositoryForCommentFeature extends JpaRepository<Comment, Integer> {

    boolean existsByUserIdAndId(Integer userId, Integer id);


    void deleteByUserIdAndId(Integer userId, Integer id);


}
