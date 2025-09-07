package com.istad.docuhub.feature.comment;

import com.istad.docuhub.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {


    Comment save(Comment comment);

    boolean existsById(Integer id);

    boolean existsByUuid(String uuid);

    Optional<Comment> findById(Integer id);

    void delete(Comment comment);

    long countByPaperId(Integer paperId);

    List<Comment> findByPaperId(Integer paperId);
}
