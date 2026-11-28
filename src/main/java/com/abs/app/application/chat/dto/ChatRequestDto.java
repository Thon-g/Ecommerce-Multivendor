package com.abs.app.application.chat.dto;

import com.abs.app.common.constant.ChatConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequestDto {

    @NotBlank(message = ChatConstant.MESSAGE_REQUIRED)
    @Size(max = 1000, message = ChatConstant.MESSAGE_TOO_LONG)
    private String message;

    private String conversationId;

    private String guestSessionId;
}
