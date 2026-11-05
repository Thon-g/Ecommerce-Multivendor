package com.abs.app.application.admin.banner.dto;

import com.abs.app.common.constant.BannerConstant;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DealRequestDto {

    @NotNull(message = BannerConstant.DISCOUNT_REQUIRED)
    @Min(value = 1, message = BannerConstant.DISCOUNT_MIN_INVALID)
    @Max(value = 100, message = BannerConstant.DISCOUNT_MAX_INVALID)
    private Integer discount;

    @NotNull(message = BannerConstant.HOME_CATEGORY_ID_REQUIRED)
    private Long homeCategoryId;
}
