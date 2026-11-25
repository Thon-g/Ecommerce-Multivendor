package com.abs.app.application.chat.dto;

import com.abs.app.common.constant.ChatConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestMigrationRequestDto {

    @NotBlank(message = ChatConstant.GUEST_SESSION_ID_REQUIRED)
    private String guestSessionId;
}
