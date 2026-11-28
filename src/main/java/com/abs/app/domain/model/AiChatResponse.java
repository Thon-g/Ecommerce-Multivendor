package com.abs.app.domain.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiChatResponse {
    private String text;

    @Builder.Default
    private List<String> recommendedProductIds = new ArrayList<>();

    @Builder.Default
    private List<String> recommendedCategoryIds = new ArrayList<>();

    @Builder.Default
    private List<String> quickReplies = new ArrayList<>();
}
