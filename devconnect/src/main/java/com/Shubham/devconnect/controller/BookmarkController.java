package com.Shubham.devconnect.controller;

import com.Shubham.devconnect.dto.response.PostResponse;
import com.Shubham.devconnect.exception.ApiResponse;
import com.Shubham.devconnect.service.BookmarkService;
import com.Shubham.devconnect.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")

public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/{postId}/toggle")
    public ResponseEntity<ApiResponse<String>> toggleBookmark(@PathVariable Long postId) {
        String result = bookmarkService.toggleBookmark(postId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> bookmarkPost(@PathVariable Long postId){
        String response = bookmarkService.bookmarkPost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> removeBookmark(@PathVariable Long postId){
        String response = bookmarkService.removeBookmark(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getMyBookmarks(){
        List<PostResponse> response = bookmarkService.getMyBookmarks();
        return ResponseEntity.ok(ApiResponse.success("My bookmarks",response));
    }


}
