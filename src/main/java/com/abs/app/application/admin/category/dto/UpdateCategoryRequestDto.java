package com.abs.app.application.admin.category.dto;

import com.abs.app.common.constant.CategoryConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryRequestDto {
    @NotBlank(message = CategoryConstant.CATEGORY_NAME_REQUIRED)
    private String name;
}
