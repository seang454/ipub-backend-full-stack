package com.istad.docuhub.feature.comment;

import com.istad.docuhub.domain.Comment;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.comment.dto.*;
import com.istad.docuhub.feature.comment.mapper.CommentMapper;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.utils.CurrentUserV2;
import com.istad.docuhub.utils.QuickService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final QuickService quickService;

    @Override
    @Transactional
    public CommentResponse createComment(CreateCommentRequest commentRequest) {

        CurrentUserV2 currentUser = quickService.currentUserInfor();

        User user = userRepository.findByUuidAndIsDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Paper paper = paperRepository.findByUuid(commentRequest.paperUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        Comment parent = null;
        if (commentRequest.parentUuid() != null && !commentRequest.parentUuid().isBlank()) {
            parent = commentRepository.findByUuid(commentRequest.parentUuid())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent comment not found"));

            // Verify parent comment belongs to the same paper
            if (!parent.getPaper().getUuid().equals(paper.getUuid())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent comment doesn't belong to this paper");
            }
        }

        // ✅ Manual ID generation (your old approach)
        int id;
        int retries = 0;
        do {
            if (retries++ > 50) {
                throw new RuntimeException("Unable to generate unique ID after 50 attempts");
            }
            id = new Random().nextInt(1000000); // Fixed: removed Integer.parseInt()
        } while (commentRepository.existsById(id));

        Comment comment = new Comment();
        comment.setId(id); // ✅ Set the manually generated ID
        comment.setUuid(UUID.randomUUID().toString());
        comment.setContent(commentRequest.content());
        comment.setCreatedAt(LocalDate.now());
        comment.setIsDeleted(false);
        comment.setUser(user);
        comment.setPaper(paper);
        comment.setParent(parent);

        Comment savedComment = commentRepository.save(comment);

        // If this is a reply, add it to parent's replies
        if (parent != null) {
            parent.getReplies().add(savedComment);
            commentRepository.save(parent); // ✅ Save the parent to update its replies
        }

        return commentMapper.toCommentResponse(savedComment);
    }
    // allow to edit only user owner and admin
    @Override
    @Transactional
    public CommentResponse editComment(UpdateCommentRequest updateCommentRequest) {
        Comment comment = commentRepository.findByUuid(updateCommentRequest.commentUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        CurrentUserV2 currentUser = quickService.currentUserInfor();

        // Handle null roles by using an empty list
        List<String> roles = currentUser.getRoles() != null ? currentUser.getRoles() : List.of();

        boolean isAdmin = roles.contains("ADMIN");
        boolean isOwner = comment.getUser().getUuid().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to edit this comment");
        }

        comment.setContent(updateCommentRequest.content());
        Comment commentSaved = commentRepository.save(comment);
        return commentMapper.toCommentResponse(commentSaved);
    }

    // allow to delete only user owner and admin
    @Override
    @Transactional
    public void deleteCommentByUuid(String commentUuid) {
        CurrentUserV2 currentUser = quickService.currentUserInfor();

        // Handle null roles by using an empty list
        List<String> roles = currentUser.getRoles() != null ? currentUser.getRoles() : List.of();

        Comment comment = commentRepository.findByUuid(commentUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        boolean isAdmin = roles.contains("ADMIN");
        boolean isOwner = comment.getUser().getUuid().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to delete this comment");
        }

        // Soft delete: mark as deleted instead of physically removing
        comment.setIsDeleted(true);

        // Also soft delete all replies
        if (!comment.getReplies().isEmpty()) {
            comment.getReplies().forEach(reply -> reply.setIsDeleted(true));
        }

        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse getCommentByUuid(String commentUuid) {
        Comment comment = commentRepository.findByUuid(commentUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (comment.getIsDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment has been deleted");
        }

        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentTreeResponse getCommentsForPaper(String paperUuid) {
        // Verify paper exists
        if (!paperRepository.existsByUuid(paperUuid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found");
        }

        // Get all non-deleted root comments for this paper
        List<Comment> rootComments = commentRepository.findByPaper_UuidAndParentIsNullAndIsDeletedFalse(paperUuid);

        // Convert to response with nested replies
        List<CommentResponse> commentResponses = rootComments.stream()
                .map(this::buildCommentTree)
                .collect(Collectors.toList());

        return new CommentTreeResponse(paperUuid, commentResponses);
    }

    // Recursive method to build comment tree with replies
    private CommentResponse buildCommentTree(Comment comment) {
        List<CommentResponse> replyResponses = comment.getReplies().stream()
                .filter(reply -> !reply.getIsDeleted()) // Filter out deleted replies
                .map(this::buildCommentTree)
                .collect(Collectors.toList());

        return commentMapper.toCommentResponseWithReplies(comment);
    }
}