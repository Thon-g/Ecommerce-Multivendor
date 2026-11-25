package com.abs.app.application.chat.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AskChatbotQuery {
    private String userId;
    private String conversationId;
    private String guestSessionId;
    private String message;
}
