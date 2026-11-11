package com.abs.app.application.admin.usermanager.dto;

import com.abs.app.application.user.address.dto.AddressResponseDto;
import com.abs.app.domain.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponseDto {
    private String userId;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String picture;
    private boolean isReceiveEmail;
    private boolean gender;
    private UserStatus status;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private Set<AddressResponseDto> addresses;
    private Integer totalOrders;
    private Long totalSpent;
}
