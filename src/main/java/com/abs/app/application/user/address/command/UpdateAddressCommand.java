package com.abs.app.application.user.address.command;

import com.abs.app.application.user.address.dto.AddressRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressCommand {
    private String userId;
    private Long addressId;
    private AddressRequestDto requestDto;
}
