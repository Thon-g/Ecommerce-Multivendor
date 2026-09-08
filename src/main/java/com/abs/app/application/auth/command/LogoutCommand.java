package com.abs.app.application.auth.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogoutCommand {
    private String userId;
    private String refreshToken;
}
