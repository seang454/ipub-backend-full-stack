package com.istad.docuhub.feature.comment;

import com.istad.docuhub.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Optional<Comment> findByUuid(String uuid);

    boolean existsByUser_Uuid(String userUuid);

    void deleteByUser_Uuid(String userUuid);

    // Fetch root comments for a paper (parent = null) and not deleted
    List<Comment> findByPaper_UuidAndParentIsNullAndIsDeletedFalse(String paperUuid);

    // Fetch all comments (including replies) for a paper
    List<Comment> findByPaper_UuidAndIsDeletedFalse(String paperUuid);

    // Fetch replies for a specific comment
    List<Comment> findByParent_UuidAndIsDeletedFalse(String parentUuid);

    // Check if a comment belongs to a specific paper
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Comment c WHERE c.uuid = :commentUuid AND c.paper.uuid = :paperUuid")
    boolean existsByUuidAndPaperUuid(@Param("commentUuid") String commentUuid,
                                     @Param("paperUuid") String paperUuid);

    // Soft delete comment and its replies
    @Query("UPDATE Comment c SET c.isDeleted = true WHERE c.uuid = :commentUuid OR c.parent.uuid = :commentUuid")
    void softDeleteCommentAndReplies(@Param("commentUuid") String commentUuid);
}