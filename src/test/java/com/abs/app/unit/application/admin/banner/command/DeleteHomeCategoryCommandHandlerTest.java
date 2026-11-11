package com.abs.app.unit.application.admin.banner.command;

import com.abs.app.application.admin.banner.command.DeleteHomeCategoryCommand;
import com.abs.app.application.admin.banner.command.DeleteHomeCategoryCommandHandler;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import com.abs.app.domain.repository.HomeCategoryRepository;
import com.abs.app.infrastructure.persistence.jpa.DealJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteHomeCategoryCommandHandlerTest {

    @Mock
    private HomeCategoryRepository homeCategoryRepository;

    @Mock
    private DealJpaRepository dealJpaRepository;

    @InjectMocks
    private DeleteHomeCategoryCommandHandler handler;

    @Test
    @DisplayName("Should delete home category and cascade delete associated deal successfully")
    void handle_Success() {
        HomeCategory existing = new HomeCategory(1L, "Category", "img.png", "cat1", HomeCategorySection.GRID);
        Deal associatedDeal = new Deal(10L, 20, existing);

        when(homeCategoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(dealJpaRepository.findByCategoryId(1L)).thenReturn(Optional.of(associatedDeal));

        handler.handle(new DeleteHomeCategoryCommand(1L));

        verify(dealJpaRepository).delete(associatedDeal);
        verify(homeCategoryRepository).delete(existing);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when home category not found")
    void handle_NotFound() {
        when(homeCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new DeleteHomeCategoryCommand(99L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(BannerConstant.HOME_CATEGORY_NOT_FOUND);

        verify(homeCategoryRepository, never()).delete(any());
    }
}

