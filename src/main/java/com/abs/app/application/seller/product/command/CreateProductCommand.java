package com.abs.app.application.seller.product.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.abs.app.application.seller.product.dto.SkuRequestDto;
import org.springframework.web.multipart.MultipartFile;
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
    private List<SkuRequestDto> skus;
    private List<MultipartFile> images;
    private String categoryId;
    private String currentUserId;
}
