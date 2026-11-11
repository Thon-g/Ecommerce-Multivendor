package com.abs.app.unit.application.admin.banner.command;

import com.abs.app.application.admin.banner.command.DeleteDealCommand;
import com.abs.app.application.admin.banner.command.DeleteDealCommandHandler;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.repository.DealRepository;
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
class DeleteDealCommandHandlerTest {

    @Mock
    private DealRepository dealRepository;

    @InjectMocks
    private DeleteDealCommandHandler handler;

    @Test
    @DisplayName("Should delete deal successfully")
    void handle_Success() {
        Deal deal = new Deal(1L, 10, new HomeCategory());
        when(dealRepository.findById(1L)).thenReturn(Optional.of(deal));

        handler.handle(new DeleteDealCommand(1L));

        verify(dealRepository).delete(deal);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deal not found")
    void handle_NotFound() {
        when(dealRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new DeleteDealCommand(99L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(BannerConstant.DEAL_NOT_FOUND);

        verify(dealRepository, never()).delete(any());
    }
}

