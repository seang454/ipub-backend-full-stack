package com.istad.docuhub.feature.comment.mapper;

import com.istad.docuhub.domain.Comment;
import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {


    // Map from CreateCommentRequest -> Comment
    // Here we ignore relations (user, paper) because you will set them in service after fetching from DB
    @Mapping(target = "id", ignore = true) // JPA will generate ID
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "paper", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDate.now())")
    Comment toComment(CreateCommentRequest createCommentRequest);


    // Map from Comment -> CommentResponse
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "paper.id", target = "paperId")
    CommentResponse toCommentResponse(Comment comment);





}
