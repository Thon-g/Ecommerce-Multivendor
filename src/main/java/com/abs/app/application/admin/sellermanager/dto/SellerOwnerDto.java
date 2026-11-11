package com.abs.app.application.admin.sellermanager.dto;

import com.abs.app.domain.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerOwnerDto {
    private String userId;
    private String userName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String picture;
    private UserStatus status;
    private LocalDateTime createdAt;
}

