package com.abs.app.application.user.info.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileCommand {
    private String userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean gender;
}