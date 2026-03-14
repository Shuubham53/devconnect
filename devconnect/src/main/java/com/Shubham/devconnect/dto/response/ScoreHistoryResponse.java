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
public class ScoreHistoryResponse {
    private Long id;
    private Integer points;
    private String reason;
    private LocalDateTime createdAt;
}
