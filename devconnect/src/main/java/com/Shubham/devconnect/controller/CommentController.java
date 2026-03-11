package com.Shubham.devconnect.controller;

import com.Shubham.devconnect.dto.request.CommentRequest;
import com.Shubham.devconnect.dto.response.CommentResponse;
import com.Shubham.devconnect.exception.ApiResponse;
import com.Shubham.devconnect.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(@PathVariable Long postId, @RequestBody CommentRequest request){
        CommentResponse response = commentService.addComment(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Comment added",response));
    }

    @PostMapping("/{commentId}/reply")
    public ResponseEntity<ApiResponse<CommentResponse>> replyToComment(@PathVariable Long commentId, @RequestBody CommentRequest request){
        CommentResponse response = commentService.replyToComment(commentId, request);
        return ResponseEntity.ok(ApiResponse.success("replied to comment ",response));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsByPost(@PathVariable Long postId){
        List<CommentResponse> response = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(ApiResponse.success("All comments of post ",response));
    }
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> editComment(@PathVariable Long commentId,@RequestBody CommentRequest request){
        CommentResponse response = commentService.editComment(commentId,request);
        return ResponseEntity.ok(ApiResponse.success("Comment Updated ",response));
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId){
        String response = commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
