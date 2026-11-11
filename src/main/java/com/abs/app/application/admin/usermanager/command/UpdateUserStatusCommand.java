package com.abs.app.application.admin.usermanager.command;

import com.abs.app.domain.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserStatusCommand {
    private String userId;
    private UserStatus status;
}
