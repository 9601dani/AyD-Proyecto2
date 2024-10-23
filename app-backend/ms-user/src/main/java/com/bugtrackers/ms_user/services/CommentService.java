package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.dto.request.CommentRequest;
import com.bugtrackers.ms_user.dto.response.CommentResponse;
import com.bugtrackers.ms_user.models.Comment;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.repositories.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public List<CommentResponse> getComments() {
        List<Comment> comments = this.commentRepository.findAll();
        List<CommentResponse> commentResponses = new ArrayList<>();
        for(Comment comment : comments) {
            commentResponses.add(new CommentResponse(
                    comment.getId(),
                    comment.getUser().getUsername(),
                    comment.getComment(),
                    comment.getValue(),
                    comment.getCreatedAt()
            ));
        }
        return commentResponses;
    }

    public CommentResponse saveComment(CommentRequest commentRequest) {
        Comment newComment = new Comment();
        User user = new User();

        user.setId(commentRequest.FK_User());
        newComment.setUser(user);
        newComment.setComment(commentRequest.comment());
        newComment.setValue(commentRequest.value());
        newComment.setCreatedAt(LocalDateTime.now());
        newComment = this.commentRepository.save(newComment);

        return new CommentResponse(
                newComment.getId(),
                newComment.getUser().getUsername(),
                newComment.getComment(),
                newComment.getValue(),
                newComment.getCreatedAt()
        );
    }
}
