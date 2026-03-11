package com.Shubham.devconnect.controller;

import com.Shubham.devconnect.dto.request.PostRequest;
import com.Shubham.devconnect.dto.response.PostResponse;
import com.Shubham.devconnect.exception.ApiResponse;
import com.Shubham.devconnect.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@RequestBody PostRequest request){
        PostResponse response = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Post is created",response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Page<PostResponse> pages = postService.getAllPosts(page,size);
        return ResponseEntity.ok(ApiResponse.success("post found",pages));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>>getPostById (@PathVariable Long id){
        PostResponse response = postService.getPostById(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User posts",response));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<List<PostResponse>>>getPostsByUser (@PathVariable String username){
        List<PostResponse> response = postService.getPostsByUser(username);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User posts",response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(@PathVariable Long id,@RequestBody PostRequest request){
        PostResponse response = postService.updatePost(id,request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Post is updated",response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long id){
        String response = postService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PostResponse>>> searchPosts(@RequestParam String query){
        List<PostResponse> response = postService.searchPosts(query);
        return ResponseEntity.ok(ApiResponse.success("Post found",response));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByTag(@PathVariable String tag){
        List<PostResponse> response = postService.getPostsByTag(tag);
        return ResponseEntity.ok(ApiResponse.success("Post found of tag "+tag,response));
    }
}
