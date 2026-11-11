package com.abs.app.application.admin.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeDataResponseDto {
    private List<HomeCategoryResponseDto> grid = new ArrayList<>();
    private List<HomeCategoryResponseDto> shopByCategories = new ArrayList<>();
    private List<HomeCategoryResponseDto> electricCategories = new ArrayList<>();
    private List<HomeCategoryResponseDto> dealCategories = new ArrayList<>();
    private List<DealResponseDto> deals = new ArrayList<>();
}

