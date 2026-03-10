package com.Shubham.devconnect.dto.request;


import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String bio;
    private String avatarUrl;
    private String githubUrl;
    private String linkedinUrl;
    private String skills;
}