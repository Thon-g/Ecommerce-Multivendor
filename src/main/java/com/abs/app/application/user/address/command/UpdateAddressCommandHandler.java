package com.abs.app.application.user.address.command;

import com.abs.app.common.constant.AddressConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Address;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.AddressRepository;
import com.abs.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAddressCommandHandler {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public void handle(UpdateAddressCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        Address addressToUpdate = user.getAddresses().stream()
                .filter(addr -> addr.getId().equals(command.getAddressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(AddressConstant.ADDRESS_NOT_FOUND_OR_NOT_BELONG));

        addressToUpdate.setName(command.getRequestDto().getName());
        addressToUpdate.setLocality(command.getRequestDto().getLocality());
        addressToUpdate.setAddress(command.getRequestDto().getAddress());
        addressToUpdate.setCity(command.getRequestDto().getCity());
        addressToUpdate.setState(command.getRequestDto().getState());
        addressToUpdate.setPinCode(command.getRequestDto().getPinCode());
        addressToUpdate.setPhone(command.getRequestDto().getPhone());

        addressRepository.save(addressToUpdate);
    }
}
