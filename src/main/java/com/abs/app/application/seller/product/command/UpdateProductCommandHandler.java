package com.abs.app.application.seller.product.command;

import com.abs.app.application.seller.product.dto.ProductResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.ProductImage;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.file.FileStorageService;
import com.abs.app.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateProductCommandHandler {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ProductResponseDto handle(UpdateProductCommand command) {
        Product product = productRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductConstant.PRODUCT_NOT_FOUND));

        Seller seller = sellerRepository.findByUserId(command.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        if (!product.getSeller().getSellerId().equals(seller.getSellerId())) {
            throw new BusinessException(ProductConstant.PRODUCT_FORBIDDEN);
        }

        Category category = categoryRepository.findById(command.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND));

        product.setTitle(command.getTitle());
        product.setDescription(command.getDescription());
        product.setMrpPrice(command.getMrpPrice());
        product.setSellingPrice(command.getSellingPrice());
        product.setQuantity(command.getQuantity());
        product.setColor(command.getColor());
        product.setSizes(command.getSizes());
        product.setCategory(category);

        if (command.getImages() != null && !command.getImages().isEmpty()) {
            product.getImages().clear(); // Clear existing images
            for (int i = 0; i < command.getImages().size(); i++) {
                MultipartFile file = command.getImages().get(i);
                String savePublicPath = fileStorageService.storeProduct(file, product.getId());
                ProductImage imageEntity = new ProductImage();
                imageEntity.setImageUrl(savePublicPath);
                imageEntity.setIsMainImage(i == 0);
                imageEntity.setProduct(product);
                product.getImages().add(imageEntity);
            }
        }

        int discountPercent = 0;
        if (product.getMrpPrice() != null && product.getMrpPrice() > 0 
                && product.getSellingPrice() != null) {
            discountPercent = (int) Math.round(
                ((double) (product.getMrpPrice() - product.getSellingPrice()) / product.getMrpPrice()) * 100
            );
        }
        product.setDiscountPercent(discountPercent);

        Product savedProduct = productRepository.save(product);

        return ProductMapper.toProductResponseDto(savedProduct);
    }
}
