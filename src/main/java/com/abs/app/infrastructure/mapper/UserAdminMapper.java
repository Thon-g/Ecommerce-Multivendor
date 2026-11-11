package com.abs.app.infrastructure.mapper;

import com.abs.app.application.admin.usermanager.dto.UserDetailResponseDto;
import com.abs.app.application.admin.usermanager.dto.UserResponseDto;
import com.abs.app.domain.entity.Order;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.OrderStatus;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAdminMapper {

    public static UserResponseDto toUserResponseDto(User user) {
        if (user == null) {
            return null;
        }

        String fullName = buildFullName(user.getFirstName(), user.getLastName(), user.getUserName());

        Set<String> roles = user.getRoles() != null
                ? user.getRoles().stream()
                        .map(role -> role.getRoleName() != null ? role.getRoleName().name() : "")
                        .filter(name -> !name.isEmpty())
                        .collect(Collectors.toSet())
                : Collections.emptySet();

        return UserResponseDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(fullName)
                .phoneNumber(user.getPhoneNumber())
                .picture(user.getPicture())
                .gender(user.isGender())
                .status(user.getStatus())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserDetailResponseDto toUserDetailResponseDto(User user, List<Order> orders) {
        if (user == null) {
            return null;
        }

        String fullName = buildFullName(user.getFirstName(), user.getLastName(), user.getUserName());

        Set<String> roles = user.getRoles() != null
                ? user.getRoles().stream()
                        .map(role -> role.getRoleName() != null ? role.getRoleName().name() : "")
                        .filter(name -> !name.isEmpty())
                        .collect(Collectors.toSet())
                : Collections.emptySet();

        var addressDtos = user.getAddresses() != null
                ? user.getAddresses().stream()
                        .map(AddressMapper::toAddressResponseDto)
                        .collect(Collectors.toSet())
                : Collections.<com.abs.app.application.user.address.dto.AddressResponseDto>emptySet();

        int totalOrders = orders != null ? orders.size() : 0;
        long totalSpent = orders != null ? orders.stream()
                .filter(o -> o.getOrderStatus() != OrderStatus.CANCELLED)
                .mapToLong(o -> o.getTotalSellingPrice() != null ? o.getTotalSellingPrice().longValue() : 0L)
                .sum() : 0L;

        return UserDetailResponseDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(fullName)
                .phoneNumber(user.getPhoneNumber())
                .picture(user.getPicture())
                .isReceiveEmail(user.isReceiveEmail())
                .gender(user.isGender())
                .status(user.getStatus())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updateAt(user.getUpdateAt())
                .addresses(addressDtos)
                .totalOrders(totalOrders)
                .totalSpent(totalSpent)
                .build();
    }

    private static String buildFullName(String firstName, String lastName, String userName) {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        String fullName = (first + " " + last).trim();
        return fullName.isEmpty() ? (userName != null ? userName : "") : fullName;
    }
}
