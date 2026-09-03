package com.abs.app.application.admin.category.dto;

import com.abs.app.common.constant.CategoryConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequestDto {
    @NotBlank(message = CategoryConstant.CATEGORY_NAME_REQUIRED)
    private String name;

    @NotBlank(message = CategoryConstant.CATEGORY_ID_REQUIRED)
    private String categoryId;

    private String parentCategoryId;

    @NotNull(message = CategoryConstant.CATEGORY_LEVEL_REQUIRED)
    private Integer level;
}
