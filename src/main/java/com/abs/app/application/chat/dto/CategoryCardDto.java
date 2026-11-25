package com.abs.app.application.chat.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryCardDto {
    private String categoryId;
    private String name;
    private Integer level;
    private String categoryUrl;
    private String imageUrl;
}
