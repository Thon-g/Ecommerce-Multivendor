package com.abs.app.unit.application.user.address.command;

import com.abs.app.application.user.address.command.CreateAddressCommand;
import com.abs.app.application.user.address.command.CreateAddressCommandHandler;
import com.abs.app.application.user.address.dto.AddressRequestDto;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAddressCommandHandlerTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateAddressCommandHandler handler;

    private CreateAddressCommand command;
    private User mockUser;
    private AddressRequestDto mockRequestDto;

    @BeforeEach
    void setUp() {
        mockRequestDto = new AddressRequestDto();
        mockRequestDto.setName("John Doe");
        mockRequestDto.setLocality("Downtown");
        mockRequestDto.setAddress("123 Main St");
        mockRequestDto.setCity("New York");
        mockRequestDto.setState("NY");
        mockRequestDto.setPinCode("10001");
        mockRequestDto.setPhone("1234567890");

        command = new CreateAddressCommand("user123", mockRequestDto);

        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setAddresses(new HashSet<>());
    }

    @Test
    @DisplayName("Thêm địa chỉ thất bại: Ném ngoại lệ khi User không tồn tại")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);

        verifyNoInteractions(addressRepository);
    }

    @Test
    @DisplayName("Thêm địa chỉ thành công")
    void shouldCreateAddressSuccessfully() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        
        Address savedAddress = new Address();
        savedAddress.setId(1L);
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);

        handler.handle(command);

        verify(addressRepository).save(any(Address.class));
        verify(userRepository).save(mockUser);
    }
}
