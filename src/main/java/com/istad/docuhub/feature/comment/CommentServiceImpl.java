package com.istad.docuhub.feature.comment;

import com.istad.docuhub.domain.Comment;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.DeleteCommentRequest;
import com.istad.docuhub.feature.comment.dto.EditCommentRequest;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final UserService userService;
    private PaperRepository paperRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;




    @Override
    public CommentResponse createComment(CreateCommentRequest createCommentRequest) {

        // Get user
        CurrentUser userId = userService.getCurrentUserSub();

        // Validation Paper
        Paper paper = paperRepository.findById(createCommentRequest.paperId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Paper not found with ID: " + createCommentRequest.paperId()
                ));
        if (!paper.getIsPublished()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paper is not published yet");
        }

        // Validation User
        User user = userRepository.findByUuidAndIsDeletedFalse(userId.id())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User does not exist"));

        // Create Comment
        Comment comment = new Comment();
        comment.setContent(createCommentRequest.content());
        comment.setCreatedAt(LocalDate.now());
        comment.setPaper(paper);
        comment.setUser(user);

        // Set comment Id
        Integer commentId;
        do {
            commentId = (int) (Math.random() * 1_000_000); // generates a number between 0 and 999999
        } while (commentRepository.existsById(commentId));
        comment.setId(commentId);

        // Set comment uuid
        String commentUuid;
        do {
            commentUuid = UUID.randomUUID().toString();
        } while (commentRepository.existsByUuid(commentUuid));


        // Save the comment
        comment = commentRepository.save(comment);

        return CommentResponse.builder()
                .id(comment.getId())
                .uuid(comment.getUuid())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .paperId(comment.getPaper().getId())
                .paperTitle(comment.getPaper().getTitle())
                .userId(comment.getUser().getId())
                .userFullName(comment.getUser().getFullName())
                .userImageUrl(comment.getUser().getImageUrl())
                .build();
    }





    @Override
    public CommentResponse editComment(EditCommentRequest editCommentRequest) {

        // Get User ID
        CurrentUser userId = userService.getCurrentUserSub();

        // Validation Comment
        if(!commentRepository.existsById(editCommentRequest.commentId())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        // Validation User
        if(!userRepository.existsByUuidAndIsDeletedFalse(userId.id())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }

        // Get comment where user wants to edit
        Comment comment = commentRepository.findById(editCommentRequest.commentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found")
        );

        // Check if user really own this comment or not
        if (!comment.getUser().getUuid().equals(userId.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this comment");
        }

        comment.setContent(editCommentRequest.content());
        comment.setCreatedAt(LocalDate.now());
        commentRepository.save(comment);


        return CommentResponse.builder()
                .id(comment.getId())
                .uuid(comment.getUuid())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .paperId(comment.getPaper().getId())
                .paperTitle(comment.getPaper().getTitle())
                .userId(comment.getUser().getId())
                .userFullName(comment.getUser().getFullName())
                .userImageUrl(comment.getUser().getImageUrl())
                .build();
    }






    @Override
    public void deleteComment(DeleteCommentRequest deleteCommentRequest) {

        // Get User ID
        CurrentUser userId = userService.getCurrentUserSub();

        // Fetch comment
        Comment comment = commentRepository.findById(deleteCommentRequest.commentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found"
                ));

        // Check if user owns the comment
        if (!comment.getUser().getUuid().equals(userId.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this comment");
        }

        // Delete comment
        commentRepository.delete(comment);
    }




    @Override
    public long countByPaperId(Integer paperId) {
        return commentRepository.countByPaperId(paperId);
    }




    @Override
    public List<CommentResponse> getCommentsByPaperId(Integer paperId) {
        List<Comment> comments = commentRepository.findByPaperId(paperId);


        return comments.stream()
                .map(comment -> CommentResponse.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .paperId(comment.getPaper().getId())
                        .paperTitle(comment.getPaper().getTitle())
                        .userId(comment.getUser().getId())
                        .userFullName(comment.getUser().getFullName())
                        .userImageUrl(comment.getUser().getImageUrl())
                        .build())
                .toList();
    }

}
