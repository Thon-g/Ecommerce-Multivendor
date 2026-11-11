package com.abs.app.application.admin.banner.dto;

import com.abs.app.common.constant.BannerConstant;
import com.abs.app.domain.entity.enums.HomeCategorySection;
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
public class UpdateHomeCategoryRequestDto {

    @Size(max = 100, message = BannerConstant.HOME_CATEGORY_NAME_SIZE_INVALID)
    private String name;

    private String categoryId;

    private HomeCategorySection section;

    private MultipartFile image;
}
