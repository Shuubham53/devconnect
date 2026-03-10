package com.Shubham.devconnect.dto.response;


import com.Shubham.devconnect.enums.PostStatus;
import com.Shubham.devconnect.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String tags;
    private PostType postType;
    private PostStatus status;
    private Integer viewCount;
    private String authorName;
    private String authorUsername;
    private Long authorId;
    private int likesCount;
    private int commentsCount;
    private int bookmarksCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}