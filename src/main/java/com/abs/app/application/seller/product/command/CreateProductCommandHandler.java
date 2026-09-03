package com.abs.app.application.seller.product.command;

import com.abs.app.application.seller.product.dto.ProductResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.util.GenerateIdUtil;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.ProductImage;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateProductCommandHandler {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ProductResponseDto handle(CreateProductCommand command) {
        Seller seller = sellerRepository.findByUserId(command.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        if (seller.getStatus() != SellerStatus.ACTIVE) {
            throw new BusinessException(SellerConstant.SELLER_NOT_ACTIVE);
        }

        Category category = categoryRepository.findById(command.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND));

        Product product = new Product();
        product.setId(GenerateIdUtil.GenerateId());
        product.setSeller(seller);
        product.setCategory(category);
        
        product.setTitle(command.getTitle());
        product.setDescription(command.getDescription());
        product.setMrpPrice(command.getMrpPrice());
        product.setSellingPrice(command.getSellingPrice());
        product.setQuantity(command.getQuantity());
        product.setColor(command.getColor());
        product.setSizes(command.getSizes());
        product.setCreateAt(LocalDateTime.now());
        product.setNumRatings(0);

        if (command.getImages() != null && !command.getImages().isEmpty()) {
            List<ProductImage> imageList = new ArrayList<>();
            for (int i = 0; i < command.getImages().size(); i++) {
                MultipartFile file = command.getImages().get(i);
                String savePublicPath = fileStorageService.storeProduct(file, product.getId());
                ProductImage imageEntity = new ProductImage();
                imageEntity.setImageUrl(savePublicPath);
                imageEntity.setIsMainImage(i == 0);
                imageEntity.setProduct(product);
                imageList.add(imageEntity);
            }
            product.setImages(imageList);
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
