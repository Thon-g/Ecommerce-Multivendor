package com.abs.app.unit.application.user.address.command;

import com.abs.app.application.user.address.command.UpdateAddressCommand;
import com.abs.app.application.user.address.command.UpdateAddressCommandHandler;
import com.abs.app.application.user.address.dto.AddressRequestDto;
import com.abs.app.common.constant.AddressConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.AddressRepository;
import com.abs.app.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAddressCommandHandlerTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateAddressCommandHandler handler;

    private UpdateAddressCommand command;
    private User mockUser;
    private Address mockAddress;
    private AddressRequestDto mockRequestDto;

    @BeforeEach
    void setUp() {
        mockRequestDto = new AddressRequestDto();
        mockRequestDto.setName("Updated Name");
        mockRequestDto.setLocality("Updated Locality");
        mockRequestDto.setAddress("456 Updated St");
        mockRequestDto.setCity("Updated City");
        mockRequestDto.setState("Updated State");
        mockRequestDto.setPinCode("20002");
        mockRequestDto.setPhone("0987654321");

        command = new UpdateAddressCommand("user123", 1L, mockRequestDto);

        mockAddress = new Address();
        mockAddress.setId(1L);
        mockAddress.setName("Old Name");

        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setAddresses(new HashSet<>(List.of(mockAddress)));
    }

    @Test
    @DisplayName("Cập nhật địa chỉ thất bại: Ném ngoại lệ khi User không tồn tại")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);

        verifyNoInteractions(addressRepository);
    }

    @Test
    @DisplayName("Cập nhật địa chỉ thất bại: Ném ngoại lệ khi Address không tồn tại hoặc không thuộc User")
    void shouldThrowResourceNotFoundException_WhenAddressNotFoundOrNotBelong() {
        mockUser.setAddresses(new HashSet<>()); // empty addresses

        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(AddressConstant.ADDRESS_NOT_FOUND_OR_NOT_BELONG);

        verifyNoInteractions(addressRepository);
    }

    @Test
    @DisplayName("Cập nhật địa chỉ thành công")
    void shouldUpdateAddressSuccessfully() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));

        handler.handle(command);

        assertThat(mockAddress.getName()).isEqualTo("Updated Name");
        assertThat(mockAddress.getAddress()).isEqualTo("456 Updated St");

        verify(addressRepository).save(mockAddress);
    }
}
