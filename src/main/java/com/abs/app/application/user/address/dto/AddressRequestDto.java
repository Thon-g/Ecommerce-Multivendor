package com.abs.app.application.user.address.dto;

import com.abs.app.common.constant.AddressConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {
    @NotBlank(message = AddressConstant.ADDRESS_NAME_REQUIRED)
    private String name;
    
    @NotBlank(message = AddressConstant.ADDRESS_LOCALITY_REQUIRED)
    private String locality;
    
    @NotBlank(message = AddressConstant.ADDRESS_REQUIRED)
    private String address;
    
    @NotBlank(message = AddressConstant.ADDRESS_CITY_REQUIRED)
    private String city;
    
    @NotBlank(message = AddressConstant.ADDRESS_STATE_REQUIRED)
    private String state;
    
    @NotBlank(message = AddressConstant.ADDRESS_PIN_CODE_REQUIRED)
    private String pinCode;
    
    @NotBlank(message = AddressConstant.ADDRESS_PHONE_REQUIRED)
    private String phone;
}
