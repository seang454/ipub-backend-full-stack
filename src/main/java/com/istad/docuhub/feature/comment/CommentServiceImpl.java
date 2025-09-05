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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {




    private CurrentUserUtil currentUserUtil;
    private PaperRepository paperRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;




    @Override
    public CommentResponse createComment(CreateCommentRequest createCommentRequest) {

        // Get User ID
        Integer userId = currentUserUtil.getCurrentUserId();

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User does not exist"));

        // Create Comment
        Comment comment = new Comment();
        comment.setContent(createCommentRequest.content());
        comment.setCreatedAt(LocalDate.now());
        comment.setPaper(paper);
        comment.setUser(user);
        // Save the comment
        comment = commentRepository.save(comment);

        return CommentResponse.builder()
                .id(comment.getId())
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
        Integer userId = currentUserUtil.getCurrentUserId();


        // Validation Comment
        if(!commentRepository.existsById(editCommentRequest.commentId())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }


        // Validation User
        if(!userRepository.existsById(userId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }


        // Get comment where user wants to edit
        Comment comment = commentRepository.findById(editCommentRequest.commentId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found")
        );


        // Check if user really own this comment or not
        if (!comment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this comment");
        }

        comment.setContent(editCommentRequest.content());
        comment.setCreatedAt(LocalDate.now());
        commentRepository.save(comment);


        return CommentResponse.builder()
                .id(comment.getId())
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

        // Get current user ID
        Integer userId = currentUserUtil.getCurrentUserId();

        // Fetch comment
        Comment comment = commentRepository.findById(deleteCommentRequest.commentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found"
                ));

        // Check if user owns the comment
        if (!comment.getUser().getId().equals(userId)) {
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
