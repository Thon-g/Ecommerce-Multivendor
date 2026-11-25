package com.abs.app.application.chat.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteChatSessionCommand {
    private String sessionId;
    private String userId;
}
