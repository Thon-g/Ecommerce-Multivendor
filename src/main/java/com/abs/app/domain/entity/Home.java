package com.abs.app.domain.entity;

import lombok.Data;

import java.util.List;

@Data
public class Home {
    private List<HomeCategory> gird;

    private List<HomeCategory> shopByCategories;

    private List<HomeCategory> electricCategories;

    private List<HomeCategory> dealCategories;

    private List<Deal> deals;
}
