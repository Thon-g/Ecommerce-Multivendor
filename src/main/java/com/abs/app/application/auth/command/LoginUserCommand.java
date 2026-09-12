package com.abs.app.application.auth.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginUserCommand {
    private final String email;
    private final String password;
    private final boolean isRememberMe;
}
