package com.abs.app.application.chat.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatHistoryQuery {
    private String sessionId;
    private String userId;
    private int page;
    private int limit;
}
