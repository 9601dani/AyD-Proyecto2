package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.request.CommentRequest;
import com.bugtrackers.ms_user.dto.response.CommentResponse;
import com.bugtrackers.ms_user.services.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("")
    public List<CommentResponse> getComments() {
        return this.commentService.getComments();
    }

    @PostMapping("")
    public CommentResponse saveComment(@RequestBody CommentRequest commentRequest) {
        return this.commentService.saveComment(commentRequest);
    }
}
