package com.abs.app.application.admin.banner.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDealCommand {
    private Integer discount;
    private Long homeCategoryId;
}

