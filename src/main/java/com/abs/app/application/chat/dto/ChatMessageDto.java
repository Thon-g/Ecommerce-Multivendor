package com.abs.app.application.chat.dto;

import com.abs.app.domain.entity.enums.ChatSenderType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private ChatSenderType senderType;
    private String content;
    private ChatMessageMetadataDto metadata;
    private LocalDateTime createdAt;
}
