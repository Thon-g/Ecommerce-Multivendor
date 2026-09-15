package com.abs.app.application.user.info.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDto {
    private String userId;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String picture;
    private boolean isReceiveEmail;
    private boolean gender;
    private String status;
    private String role;
    private Set<String> addresses;
}
