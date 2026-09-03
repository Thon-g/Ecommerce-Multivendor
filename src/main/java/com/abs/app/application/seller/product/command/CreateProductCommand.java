package com.abs.app.application.seller.product.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCommand {
    private String title;
    private String description;
    private Integer mrpPrice;
    private Integer sellingPrice;
    private Integer quantity;
    private String color;
    private String sizes;
    private List<String> images;
    private String categoryId;
    private String currentUserId;
}
