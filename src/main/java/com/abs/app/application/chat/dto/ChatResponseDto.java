package com.abs.app.application.chat.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponseDto {

    private String conversationId;

    private String reply;

    @Builder.Default
    private List<ProductCardDto> products = new ArrayList<>();

    @Builder.Default
    private List<CategoryCardDto> categories = new ArrayList<>();

    @Builder.Default
    private List<String> quickReplies = new ArrayList<>();
}
