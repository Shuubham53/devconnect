package com.Shubham.devconnect.dto.response;

import com.Shubham.devconnect.enums.PostStatus;
import com.Shubham.devconnect.enums.PostType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
    private String imageUrl;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
}