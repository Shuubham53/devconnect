package com.Shubham.devconnect.controller;

import com.Shubham.devconnect.dto.request.UpdateProfileRequest;
import com.Shubham.devconnect.dto.response.UserResponse;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.exception.ApiResponse;
import com.Shubham.devconnect.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String username){
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("User found",user));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody UpdateProfileRequest profileRequest){
        UserResponse user = userService.updateProfile(profileRequest);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully",user));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(
            @RequestParam String query) {
        List<UserResponse> users = userService.searchUsers(query);
        return ResponseEntity.ok(ApiResponse.success(
                "Search results", users));
    }

}
