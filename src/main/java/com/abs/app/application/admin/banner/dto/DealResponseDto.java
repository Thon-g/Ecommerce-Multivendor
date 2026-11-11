package com.abs.app.application.admin.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DealResponseDto {
    private Long id;
    private Integer discount;
    private HomeCategoryResponseDto category;
}

