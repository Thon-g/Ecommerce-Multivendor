package com.abs.app.application.chat.dto;

import com.abs.app.domain.entity.enums.ChatSessionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSessionSummaryDto {
    private String id;
    private String title;
    private ChatSessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
