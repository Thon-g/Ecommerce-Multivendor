package com.abs.app.application.chat.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatHistoryQuery {
    private String sessionId;
    private String userId;
    private Pageable pageable;
}
