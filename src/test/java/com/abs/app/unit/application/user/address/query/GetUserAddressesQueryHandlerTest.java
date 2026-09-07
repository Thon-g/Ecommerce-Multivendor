package com.abs.app.unit.application.user.address.query;

import com.abs.app.application.user.address.dto.AddressResponseDto;
import com.abs.app.application.user.address.query.GetUserAddressesQueryHandler;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserAddressesQueryHandlerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserAddressesQueryHandler handler;

    private User mockUser;
    private Address mockAddress;

    @BeforeEach
    void setUp() {
        mockAddress = new Address();
        mockAddress.setId(1L);
        mockAddress.setName("Home");
        mockAddress.setAddress("123 Street");
        mockAddress.setCity("Ho Chi Minh");

        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setAddresses(Set.of(mockAddress));
    }

    @Test
    @DisplayName("Lấy danh sách địa chỉ thất bại: Ném ngoại lệ khi User không tồn tại")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle("user123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);
    }

    @Test
    @DisplayName("Lấy danh sách địa chỉ thành công: Trả về danh sách DTO của các địa chỉ")
    void shouldReturnAddressList_WhenUserExists() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));

        List<AddressResponseDto> result = handler.handle("user123");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Home");
        assertThat(result.get(0).getAddress()).isEqualTo("123 Street");
        assertThat(result.get(0).getCity()).isEqualTo("Ho Chi Minh");

        verify(userRepository).findById("user123");
    }
}
