package com.abs.app.domain.model;

import com.abs.app.domain.entity.enums.ChatSenderType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestChatMessage {
    private ChatSenderType senderType;
    private String content;
    private String metadataJson;
    private LocalDateTime createdAt;
}
