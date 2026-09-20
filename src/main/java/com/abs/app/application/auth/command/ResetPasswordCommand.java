package com.abs.app.application.auth.command;

import lombok.Value;

@Value
public class ResetPasswordCommand {
    String token;
    String newPassword;
}
