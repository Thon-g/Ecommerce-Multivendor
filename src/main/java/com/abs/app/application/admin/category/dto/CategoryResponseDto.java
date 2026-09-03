package com.abs.app.application.admin.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    private String id;
    private String name;
    private String categoryId;
    private String parentCategoryId;
    private Integer level;
    private List<CategoryResponseDto> children = new ArrayList<>();
}
