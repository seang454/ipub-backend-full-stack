package com.istad.docuhub.feature.comment;

import com.istad.docuhub.domain.Comment;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.UpdateCommentRequest;
import com.istad.docuhub.feature.comment.mapper.CommentMapper;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.utils.CurrentUserV2;
import com.istad.docuhub.utils.QuickService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private QuickService quickService;

    @Override
    public CommentResponse createComment(CreateCommentRequest commentRequest) {

        User user = userRepository.findByUuidAndIsDeletedFalse(commentRequest.userUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in System "));

        Paper paper = paperRepository.findByUuid(commentRequest.paperUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        int id;
        int retries = 0;
        do {
            if (retries++ > 50) {
                throw new RuntimeException("Unable to generate unique ID after 50 attempts");
            }
            id = new Random().nextInt(Integer.parseInt("1000000"));
        } while (commentRepository.existsById(id));


        Comment comment = new Comment();
        comment.setId(id);
        comment.setUuid(UUID.randomUUID().toString());
        comment.setContent(commentRequest.content());
        comment.setCreatedAt(LocalDate.now());
        comment.setIsDeleted(false);
        comment.setUser(user);
        comment.setPaper(paper);

        Comment saved = commentRepository.save(comment);

        return commentMapper.toCommentResponse(saved);
    }


    // allow to edit only user owner and admin
    @Override
    public CommentResponse editComment(UpdateCommentRequest updateCommentRequest) {
        Comment comment = commentRepository.findByUuid(updateCommentRequest.commentUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Comment not found"));

        CurrentUserV2 currentUser = quickService.currentUserInfor();

        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        boolean isOwner = comment.getUser().getUuid().equals(currentUser.getUuid());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to edit this comment");
        }

        comment.setContent(updateCommentRequest.content());
        comment.setCreatedAt(LocalDate.now());
        Comment commentSaved = commentRepository.save(comment);
        return commentMapper.toCommentResponse(commentSaved);
    }


    // allow to delete only user owner and admin
    @Override
    public void deleteCommentByUuid(String commentUuid) {
        CurrentUserV2 currentUser = quickService.currentUserInfor();

        Comment comment = commentRepository.findByUuid(commentUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Comment not found"));

        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        boolean isOwner = comment.getUser().getUuid().equals(currentUser.getUuid());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }


}
