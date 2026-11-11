package com.abs.app.application.admin.banner.query;

import com.abs.app.domain.entity.enums.HomeCategorySection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetHomeCategoriesQuery {
    private HomeCategorySection section;
}

