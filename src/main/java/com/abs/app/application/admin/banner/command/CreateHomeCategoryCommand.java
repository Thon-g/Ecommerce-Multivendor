package com.abs.app.application.admin.banner.command;

import com.abs.app.domain.entity.enums.HomeCategorySection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateHomeCategoryCommand {
    private String name;
    private MultipartFile image;
    private String categoryId;
    private HomeCategorySection section;
}
