package com.abs.app.application.admin.category.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAdminCategoriesPagedQuery {
    private String keyword;
    private Integer level;
    private int page = 1;
    private int size = 10;
}

