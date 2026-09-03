package com.abs.app.application.seller.product.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCommand {
    private String id;
    private String title;
    private String description;
    private Integer mrpPrice;
    private Integer sellingPrice;
    private Integer quantity;
    private String color;
    private String sizes;
    private List<MultipartFile> images;
    private String categoryId;
    private String currentUserId;
}
