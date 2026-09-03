package com.abs.app.application.seller.product.command;

import com.abs.app.application.seller.product.dto.ProductResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateProductCommandHandler {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponseDto handle(CreateProductCommand command) {
        Seller seller = sellerRepository.findByUserId(command.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        if (seller.getStatus() != SellerStatus.ACTIVE) {
            throw new BusinessException(SellerConstant.SELLER_NOT_ACTIVE);
        }

        Category category = categoryRepository.findByCategoryId(command.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND));

        Product product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setSeller(seller);
        product.setCategory(category);
        
        product.setTitle(command.getTitle());
        product.setDescription(command.getDescription());
        product.setMrpPrice(command.getMrpPrice());
        product.setSellingPrice(command.getSellingPrice());
        product.setQuantity(command.getQuantity());
        product.setColor(command.getColor());
        product.setSizes(command.getSizes());
        product.setImages(command.getImages());
        product.setCreateAt(LocalDateTime.now());
        product.setNumRatings(0);

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
