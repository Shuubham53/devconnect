package com.Shubham.devconnect.controller;

import com.Shubham.devconnect.exception.ApiResponse;
import com.Shubham.devconnect.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> likePost(@PathVariable Long postId){
        String response = likeService.likePost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> unlikePost(@PathVariable Long postId){
        String response = likeService.unlikePost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<Long>> getLikesCount(@PathVariable Long postId){
        long response = likeService.getLikesCount(postId);
        return ResponseEntity.ok(ApiResponse.success("likes count",response));
    }


}
