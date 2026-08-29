package com.abs.app.infrastructure.mapper;

import com.abs.app.application.user.address.dto.AddressResponseDto;
import com.abs.app.domain.entity.Address;

public class AddressMapper {

    public static AddressResponseDto toAddressResponseDto(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponseDto.builder()
                .id(address.getId())
                .name(address.getName())
                .locality(address.getLocality())
                .address(address.getAddress())
                .city(address.getCity())
                .state(address.getState())
                .pinCode(address.getPinCode())
                .phone(address.getPhone())
                .build();
    }
}
