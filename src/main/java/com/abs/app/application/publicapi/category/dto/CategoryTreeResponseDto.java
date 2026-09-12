package com.abs.app.application.publicapi.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponseDto {
    private String categoryId;
    private String name;
    private String parentCategoryId;
    private Integer level;
    private List<CategoryTreeResponseDto> children = new ArrayList<>();
}
