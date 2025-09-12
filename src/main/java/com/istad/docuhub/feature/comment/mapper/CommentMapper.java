package com.istad.docuhub.feature.comment.mapper;

import com.istad.docuhub.domain.Comment;
import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "paper", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "replies", ignore = true)
    Comment toComment(CreateCommentRequest createCommentRequest);

    @Mapping(source = "user.uuid", target = "userUuid")
    @Mapping(source = "paper.uuid", target = "paperUuid")
    @Mapping(source = "parent", target = "parentUuid", qualifiedByName = "mapParentToUuid")
    @Mapping(target = "replies", ignore = true)
    CommentResponse toCommentResponse(Comment comment);

    // Custom mapping for building comment tree with replies
    @Mapping(source = "user.uuid", target = "userUuid")
    @Mapping(source = "paper.uuid", target = "paperUuid")
    @Mapping(source = "parent", target = "parentUuid", qualifiedByName = "mapParentToUuid")
    @Mapping(source = "replies", target = "replies", qualifiedByName = "mapReplies")
    CommentResponse toCommentResponseWithReplies(Comment comment);

    @Named("mapParentToUuid")
    default String mapParentToUuid(Comment parent) {
        return parent != null ? parent.getUuid() : null;
    }

    @Named("mapReplies")
    default List<CommentResponse> mapReplies(List<Comment> replies) {
        if (replies == null || replies.isEmpty()) {
            return List.of();
        }

        // Filter out deleted replies and map to response
        return replies.stream()
                .filter(reply -> !reply.getIsDeleted())
                .map(this::toCommentResponseWithReplies)
                .collect(Collectors.toList());
    }
}