package com.Shubham.devconnect.controller;

import com.Shubham.devconnect.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    // Upload post image
    @PostMapping("/post-image")
    public ResponseEntity<com.Shubham.devconnect.exception.ApiResponse<String>> uploadPostImage(
            @RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryService.uploadImage(file, "posts");
        return ResponseEntity.ok(com.Shubham.devconnect.exception.ApiResponse.success(
                "Image uploaded successfully", imageUrl));
    }

    // Upload profile avatar
    @PostMapping("/avatar")
    public ResponseEntity<com.Shubham.devconnect.exception.ApiResponse<String>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryService.uploadImage(file, "avatars");
        return ResponseEntity.ok(com.Shubham.devconnect.exception.ApiResponse.success(
                "Avatar uploaded successfully", imageUrl));
    }
}