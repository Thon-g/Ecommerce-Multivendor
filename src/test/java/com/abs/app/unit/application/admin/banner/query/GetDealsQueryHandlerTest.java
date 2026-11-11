package com.abs.app.unit.application.admin.banner.query;

import com.abs.app.application.admin.banner.dto.DealResponseDto;
import com.abs.app.application.admin.banner.query.GetDealsQuery;
import com.abs.app.application.admin.banner.query.GetDealsQueryHandler;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.repository.DealRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDealsQueryHandlerTest {

    @Mock
    private DealRepository dealRepository;

    @InjectMocks
    private GetDealsQueryHandler handler;

    @Test
    @DisplayName("Should return all deals")
    void handle_Success() {
        Deal deal = new Deal(1L, 25, new HomeCategory());
        when(dealRepository.findAll()).thenReturn(List.of(deal));

        List<DealResponseDto> result = handler.handle(new GetDealsQuery());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDiscount()).isEqualTo(25);
        verify(dealRepository).findAll();
    }
}

