package com.abs.app.infrastructure.mapper;

import com.abs.app.application.chat.dto.*;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.ChatMessage;
import com.abs.app.domain.entity.ChatSession;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.ProductImage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class ChatMapper {

    public static ProductCardDto toProductCardDto(Product product) {
        if (product == null) {
            return null;
        }

        String imageUrl = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            imageUrl = product.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsMainImage()))
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElse(product.getImages().get(0).getImageUrl());
        }

        String shopName = (product.getSeller() != null) ? product.getSeller().getShopName() : "";

        return ProductCardDto.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .imageUrl(imageUrl)
                .mrpPrice(product.getMrpPrice() != null ? product.getMrpPrice().longValue() : 0L)
                .sellingPrice(product.getSellingPrice() != null ? product.getSellingPrice().longValue() : 0L)
                .discountPercent(product.getDiscountPercent())
                .rating(product.getAverageRating() != null ? product.getAverageRating() : 0.0)
                .shopName(shopName)
                .productUrl("/products/" + product.getId())
                .build();
    }

    public static CategoryCardDto toCategoryCardDto(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryCardDto.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .level(category.getLevel())
                .categoryUrl("/category/" + category.getCategoryId())
                .imageUrl(null)
                .build();
    }

    public static ChatMessageDto toChatMessageDto(ChatMessage message, ObjectMapper objectMapper) {
        if (message == null) {
            return null;
        }

        ChatMessageMetadataDto metadataDto = null;
        if (message.getMetadata() != null && !message.getMetadata().isBlank() && objectMapper != null) {
            try {
                metadataDto = objectMapper.readValue(message.getMetadata(), ChatMessageMetadataDto.class);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse chat message metadata JSON: {}", e.getMessage());
            }
        }

        if (metadataDto == null) {
            metadataDto = ChatMessageMetadataDto.builder()
                    .productIds(new ArrayList<>())
                    .categoryIds(new ArrayList<>())
                    .quickReplies(new ArrayList<>())
                    .build();
        }

        return ChatMessageDto.builder()
                .id(message.getId())
                .senderType(message.getSenderType())
                .content(message.getContent())
                .metadata(metadataDto)
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static ChatSessionSummaryDto toChatSessionSummaryDto(ChatSession session) {
        if (session == null) {
            return null;
        }
        return ChatSessionSummaryDto.builder()
                .id(session.getId())
                .title(session.getTitle())
                .status(session.getStatus())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
}
