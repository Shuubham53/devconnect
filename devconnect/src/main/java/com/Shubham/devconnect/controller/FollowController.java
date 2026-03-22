package com.Shubham.devconnect.controller;

import com.Shubham.devconnect.dto.response.UserResponse;
import com.Shubham.devconnect.exception.ApiResponse;
import com.Shubham.devconnect.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{userId}/toggle")
    public ResponseEntity<ApiResponse<String>> toggleFollow(@PathVariable Long userId) {
        String result = followService.toggleFollow(userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{userId}/is-following")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(@PathVariable Long userId) {
        boolean result = followService.isFollowing(userId);
        return ResponseEntity.ok(ApiResponse.success("status", result));
    }
    @GetMapping("/{username}/followers")
    public ResponseEntity<ApiResponse<List<UserResponse>>>getFollowers(@PathVariable String username){
        List<UserResponse> users = followService.getFollowers(username);
        return ResponseEntity.ok(ApiResponse.success("Follower list",users));
    }
    @GetMapping("/{username}/following")
    public ResponseEntity<ApiResponse<List<UserResponse>>>getFollowing(@PathVariable String username){
        List<UserResponse> users = followService.getFollowing(username);
        return ResponseEntity.ok(ApiResponse.success("Following list",users));
    }


}
