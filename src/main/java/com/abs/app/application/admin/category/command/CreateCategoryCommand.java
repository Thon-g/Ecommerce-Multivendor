package com.abs.app.application.admin.category.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryCommand {
    private String name;
    private String categoryId;
    private String parentCategoryId;
    private Integer level;
}
