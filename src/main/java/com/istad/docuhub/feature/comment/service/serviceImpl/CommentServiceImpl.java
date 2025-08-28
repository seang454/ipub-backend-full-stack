package com.istad.docuhub.feature.comment.service.serviceImpl;

import com.istad.docuhub.domain.Comment;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.DeleteCommentRequest;
import com.istad.docuhub.feature.comment.dto.EditCommentRequest;
import com.istad.docuhub.feature.comment.mapper.CommentMapper;
import com.istad.docuhub.feature.comment.repository.CommentRepositoryForCommentFeature;
import com.istad.docuhub.feature.comment.repository.PaperRepositoryForCommentFeature;
import com.istad.docuhub.feature.comment.repository.UserRepositoryForCommentFeature;
import com.istad.docuhub.feature.comment.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final UserRepositoryForCommentFeature userRepository;
    private final PaperRepositoryForCommentFeature paperRepository;
    private final CommentRepositoryForCommentFeature commentRepository;
    private final CommentMapper commentMapper;


//    @Override
//    public CommentResponse comment(CreateCommentRequest createCommentRequest) {
//
//        Comment comment = new Comment();
//
//        comment.setContent(createCommentRequest.content());
//        comment.setCreatedAt(LocalDate.now());
//
//        User user = userRepository.findById(createCommentRequest.userId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        Paper paper = paperRepository.findById(createCommentRequest.paperId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));
//
//        comment.setPaper(paper);
//        comment.setUser(user);
//
//        Comment saved = commentRepository.save(comment);
//
//
//        return commentMapper.toCommentResponse(saved);
//    }



    @Override
    public CommentResponse comment(CreateCommentRequest req) {
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Paper paper = paperRepository.findById(req.paperId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        Comment comment = commentMapper.toComment(req); // mapper sets content & createdAt
        comment.setUser(user);                          // relations set manually
        comment.setPaper(paper);
        comment.setCreatedAt(LocalDate.now());

        Comment saved = commentRepository.save(comment);
        return commentMapper.toCommentResponse(saved);
    }





    @Override
    public CommentResponse editComment(EditCommentRequest req) {
        Comment comment = commentRepository.findById(req.commentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getUser().getId().equals(req.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to edit this comment");
        }

        comment.setContent(req.content());
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
