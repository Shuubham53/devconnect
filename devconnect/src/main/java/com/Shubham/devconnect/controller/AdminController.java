package com.Shubham.devconnect.controller;

import com.Shubham.devconnect.dto.response.PostResponse;
import com.Shubham.devconnect.dto.response.UserResponse;
import com.Shubham.devconnect.exception.ApiResponse;
import com.Shubham.devconnect.service.PostService;
import com.Shubham.devconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final PostService postService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users",users));
    }
    @PutMapping("/users/{id}/ban")
    public ResponseEntity<ApiResponse<String>> banUser(@PathVariable Long id){
         String response = userService.banUser(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @PutMapping("/users/{id}/unban")
    public ResponseEntity<ApiResponse<String>> unbanUser(@PathVariable Long id){
        String response = userService.unbanUser(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id){
        String response = userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostsAdmin(){
        List<PostResponse> response = postService.getAllPostsAdmin();
        return ResponseEntity.ok(ApiResponse.success("ALL posts including flagged/deleted ",response));
    }
    @PutMapping("/posts/{id}/flag")
    public ResponseEntity<ApiResponse<String>> flagPost(@PathVariable Long id){
        String response = postService.flagPost(id);
        return ResponseEntity.ok(ApiResponse.success("Flag posts ",response));
    }
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<String>> deletePostAdmin(@PathVariable Long id){
        String response = postService.deletePostAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("post deleted successfully ",response));
    }

}
