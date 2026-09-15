package com.abs.app.unit.application.user.info.query;

import com.abs.app.application.user.info.dto.UserInfoResponseDto;
import com.abs.app.application.user.info.query.GetCurrentUserQueryHandler;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.Role;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.RoleUser;
import com.abs.app.domain.entity.enums.UserStatus;
import com.abs.app.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCurrentUserQueryHandlerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetCurrentUserQueryHandler handler;

    private User mockUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(RoleUser.CUSTOMER);

        Address address = new Address();
        address.setAddress("123 Main St, City");

        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setUserName("JohnDoe");
        mockUser.setEmail("john@gmail.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setPhoneNumber("0123456789");
        mockUser.setPicture("avatar.jpg");
        mockUser.setReceiveEmail(true);
        mockUser.setGender(true);
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setRoles(Set.of(role));
        mockUser.setAddresses(Set.of(address));
    }

    @Test
    @DisplayName("Lấy thông tin người dùng thất bại: Ném ngoại lệ khi User không tồn tại")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle("user123"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);
    }

    @Test
    @DisplayName("Lấy thông tin người dùng thành công: Trả về DTO chứa thông tin đầy đủ")
    void shouldReturnUserInfoResponseDto_WhenUserExists() {
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));

        UserInfoResponseDto response = handler.handle("user123");

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo("user123");
        assertThat(response.getUserName()).isEqualTo("JohnDoe");
        assertThat(response.getEmail()).isEqualTo("john@gmail.com");
        assertThat(response.getRole()).isEqualTo("CUSTOMER");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getAddresses()).containsExactly("123 Main St, City");
        
        verify(userRepository).findById("user123");
    }
}
