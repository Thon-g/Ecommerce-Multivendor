package com.abs.app.application.chat.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageMetadataDto {
    @Builder.Default
    private List<String> productIds = new ArrayList<>();

    @Builder.Default
    private List<String> categoryIds = new ArrayList<>();

    @Builder.Default
    private List<String> quickReplies = new ArrayList<>();
}
