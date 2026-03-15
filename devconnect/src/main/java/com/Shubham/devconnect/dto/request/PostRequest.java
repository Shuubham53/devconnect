package com.Shubham.devconnect.dto.request;


import com.Shubham.devconnect.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String tags;
    private String imageUrl;

    @NotNull(message = "Post type is required")
    private PostType postType;
}
