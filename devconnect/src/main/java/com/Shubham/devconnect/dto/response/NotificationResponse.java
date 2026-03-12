package com.Shubham.devconnect.dto.response;


import com.Shubham.devconnect.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private String senderName;
    private String senderUsername;
    private Long postId;
    private LocalDateTime createdAt;
}