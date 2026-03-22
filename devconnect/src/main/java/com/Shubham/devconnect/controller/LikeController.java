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

    @PostMapping("/{postId}/toggle")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.toggleLike(postId));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<Long>> getLikesCount(@PathVariable Long postId){
        long response = likeService.getLikesCount(postId);
        return ResponseEntity.ok(ApiResponse.success("likes count",response));
    }


}
