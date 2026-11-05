package com.abs.app.unit.application.admin.banner.command;

import com.abs.app.application.admin.banner.command.CreateDealCommand;
import com.abs.app.application.admin.banner.command.CreateDealCommandHandler;
import com.abs.app.application.admin.banner.dto.DealResponseDto;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import com.abs.app.domain.repository.DealRepository;
import com.abs.app.domain.repository.HomeCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDealCommandHandlerTest {

    @Mock
    private DealRepository dealRepository;

    @Mock
    private HomeCategoryRepository homeCategoryRepository;

    @InjectMocks
    private CreateDealCommandHandler handler;

    private HomeCategory homeCategory;

    @BeforeEach
    void setUp() {
        homeCategory = new HomeCategory(1L, "Deals Category", "deal.jpg", "electronics", HomeCategorySection.DEALS);
    }

    @Test
    @DisplayName("Should create deal successfully")
    void handle_Success() {
        CreateDealCommand command = new CreateDealCommand(20, 1L);

        when(homeCategoryRepository.findById(1L)).thenReturn(Optional.of(homeCategory));
        when(dealRepository.existsByCategoryId(1L)).thenReturn(false);
        when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> {
            Deal entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        DealResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getDiscount()).isEqualTo(20);
        assertThat(response.getCategory()).isNotNull();
        assertThat(response.getCategory().getId()).isEqualTo(1L);
        verify(dealRepository).save(any(Deal.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when discount is invalid")
    void handle_InvalidDiscount() {
        CreateDealCommand command = new CreateDealCommand(150, 1L);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(BannerConstant.INVALID_DISCOUNT_PERCENT);

        verify(dealRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when home category not found")
    void handle_HomeCategoryNotFound() {
        CreateDealCommand command = new CreateDealCommand(20, 99L);
        when(homeCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(BannerConstant.HOME_CATEGORY_NOT_FOUND);

        verify(dealRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when deal already exists for home category")
    void handle_DealAlreadyExists() {
        CreateDealCommand command = new CreateDealCommand(20, 1L);
        when(homeCategoryRepository.findById(1L)).thenReturn(Optional.of(homeCategory));
        when(dealRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(BannerConstant.DEAL_ALREADY_EXISTS);

        verify(dealRepository, never()).save(any());
    }
}

