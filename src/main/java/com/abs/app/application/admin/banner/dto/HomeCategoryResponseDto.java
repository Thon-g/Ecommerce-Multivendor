package com.abs.app.application.admin.banner.dto;

import com.abs.app.domain.entity.enums.HomeCategorySection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeCategoryResponseDto {
    private Long id;
    private String name;
    private String image;
    private String categoryId;
    private HomeCategorySection section;
}

