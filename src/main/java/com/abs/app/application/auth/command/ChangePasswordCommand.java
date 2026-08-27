package com.abs.app.application.auth.command;

import lombok.Value;

@Value
public class ChangePasswordCommand {
    private final String userId;
    private final String currentPassword;
    private final String newPassword;
    private final String rePassword;
}
