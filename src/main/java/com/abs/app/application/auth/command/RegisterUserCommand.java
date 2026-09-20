package com.abs.app.application.auth.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUserCommand {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
