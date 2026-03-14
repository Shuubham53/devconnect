package com.Shubham.devconnect.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String bio;
    private String avatarUrl;
    private String githubUrl;
    private String linkedinUrl;
    private String skills;
    private String role;
    private Integer score;
    private String badge;
    private int followersCount;
    private int followingCount;
    private LocalDateTime createdAt;
}