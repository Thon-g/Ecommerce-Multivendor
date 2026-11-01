package com.abs.app.application.admin.usermanager.query;

import com.abs.app.domain.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUsersQuery {
    private String keyword;
    private UserStatus status;
    private int page = 1;
    private int size = 10;
}
