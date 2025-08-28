package com.istad.docuhub.feature.comment.service.serviceImpl;

import com.istad.docuhub.Repository.CommentRepository;
import com.istad.docuhub.Repository.PaperRepository;
import com.istad.docuhub.Repository.UserRepository;
import com.istad.docuhub.domain.Comment;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.DeleteCommentRequest;
import com.istad.docuhub.feature.comment.dto.EditCommentRequest;
import com.istad.docuhub.feature.comment.mapper.CommentMapper;
import com.istad.docuhub.feature.comment.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    public CommentResponse comment(CreateCommentRequest createCommentRequest) {

        Comment comment = new Comment();

        comment.setContent(createCommentRequest.content());
        comment.setCreatedAt(LocalDate.now());

        // Validation User
        User user = userRepository.findUserById(createCommentRequest.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Validation Paper
        Paper paper = paperRepository.findPaperById(createCommentRequest.paperId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        comment.setPaper(paper);
        comment.setUser(user);

        Comment saved = commentRepository.save(comment);


        return commentMapper.toCommentResponse(saved);
    }





    @Override
    public CommentResponse editComment(EditCommentRequest editCommentRequest) {

        Comment comment = commentRepository.findById(editCommentRequest.commentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        comment.setContent(editCommentRequest.content());

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }


    @Override
    public void deleteCommentByUserIdAndCommentId(DeleteCommentRequest deleteCommentRequest) {

        if(!commentRepository.existsByUserIdAndId(deleteCommentRequest.userId(), deleteCommentRequest.commentId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Comment not found or you don't have permission to delete it");
        }

        commentRepository.deleteByUserIdAndId(deleteCommentRequest.userId(), deleteCommentRequest.commentId());

    }


}
