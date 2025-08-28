package com.istad.docuhub.Repository;

import com.istad.docuhub.domain.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    boolean existsById(Integer id);


    boolean existsByUserIdAndId(Integer userId, Integer commentId);

    @Modifying
    @Transactional
    void deleteByUserIdAndId(Integer userId, Integer commentId);

}
