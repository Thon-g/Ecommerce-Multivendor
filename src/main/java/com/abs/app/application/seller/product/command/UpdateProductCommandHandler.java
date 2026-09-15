package com.abs.app.application.seller.product.command;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.application.seller.product.dto.SkuRequestDto;
import com.abs.app.application.seller.product.command.UpdateProductCommand;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.ProductImage;
import com.abs.app.domain.entity.ProductSku;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
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
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ProductResponseDto handle(UpdateProductCommand command) {
        Seller seller = sellerRepository.findByUserId(command.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        if (seller.getStatus() != SellerStatus.ACTIVE) {
            throw new BusinessException(SellerConstant.SELLER_NOT_ACTIVE);
        }

        Product product = productRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductConstant.PRODUCT_NOT_FOUND));

        if (!product.getSeller().getSellerId().equals(seller.getSellerId())) {
            throw new BusinessException(ProductConstant.PRODUCT_FORBIDDEN);
        }

        Category category = categoryRepository.findById(command.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND));

        product.setTitle(command.getTitle());
        product.setDescription(command.getDescription());
        product.setMrpPrice(command.getMrpPrice());
        product.setSellingPrice(command.getSellingPrice());
        if (command.getSkus() != null) {
            List<ProductSku> existingSkus = product.getSkus();
            List<ProductSku> updatedSkus = new ArrayList<>();

            for (SkuRequestDto skuDto : command.getSkus()) {
                ProductSku matchingSku = existingSkus.stream()
                        .filter(s -> 
                            (s.getSkuCode() != null && s.getSkuCode().equals(skuDto.getSkuCode())) || 
                            (s.getColor() != null && s.getColor().equals(skuDto.getColor()) && s.getSize() != null && s.getSize().equals(skuDto.getSize()))
                        )
                        .findFirst()
                        .orElse(null);

                if (matchingSku != null) {
                    matchingSku.setSkuCode(skuDto.getSkuCode());
                    matchingSku.setColor(skuDto.getColor());
                    matchingSku.setSize(skuDto.getSize());
                    matchingSku.setQuantity(skuDto.getQuantity() != null ? skuDto.getQuantity() : 0);
                    matchingSku.setSellingPrice(skuDto.getSellingPrice());
                    updatedSkus.add(matchingSku);
                } else {
                    ProductSku sku = new ProductSku();
                    sku.setProduct(product);
                    sku.setSkuCode(skuDto.getSkuCode());
                    sku.setColor(skuDto.getColor());
                    sku.setSize(skuDto.getSize());
                    sku.setQuantity(skuDto.getQuantity() != null ? skuDto.getQuantity() : 0);
                    sku.setSellingPrice(skuDto.getSellingPrice());
                    updatedSkus.add(sku);
                }
            }

            // Soft-disable missing SKUs by setting quantity to 0 instead of removing them
            for (ProductSku existingSku : existingSkus) {
                boolean isProcessed = false;
                for (ProductSku updatedSku : updatedSkus) {
                    if (existingSku == updatedSku) {
                        isProcessed = true;
                        break;
                    }
                }
                if (!isProcessed) {
                    existingSku.setQuantity(0);
                }
            }

            // Add new SKUs
            for (ProductSku updatedSku : updatedSkus) {
                boolean isNew = true;
                for (ProductSku existingSku : existingSkus) {
                    if (existingSku == updatedSku) {
                        isNew = false;
                        break;
                    }
                }
                if (isNew) {
                    existingSkus.add(updatedSku);
                }
            }
        }
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
