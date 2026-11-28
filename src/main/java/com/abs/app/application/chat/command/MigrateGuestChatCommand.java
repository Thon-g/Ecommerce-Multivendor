package com.abs.app.application.chat.command;

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
public class MigrateGuestChatCommand {
    private String userId;
    private String guestSessionId;
}
