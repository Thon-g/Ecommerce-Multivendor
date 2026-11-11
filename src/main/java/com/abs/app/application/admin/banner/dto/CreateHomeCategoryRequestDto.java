package com.abs.app.application.admin.banner.dto;

import com.abs.app.common.constant.BannerConstant;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateHomeCategoryRequestDto {

    @NotBlank(message = BannerConstant.HOME_CATEGORY_NAME_REQUIRED)
    @Size(max = 100, message = BannerConstant.HOME_CATEGORY_NAME_SIZE_INVALID)
    private String name;

    @NotBlank(message = BannerConstant.CATEGORY_ID_REQUIRED)
    private String categoryId;

    @NotNull(message = BannerConstant.SECTION_REQUIRED)
    private HomeCategorySection section;

    @NotNull(message = BannerConstant.BANNER_IMAGE_REQUIRED)
    private MultipartFile image;
}
