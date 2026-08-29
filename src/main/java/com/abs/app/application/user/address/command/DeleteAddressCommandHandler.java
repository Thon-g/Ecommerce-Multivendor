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
public class DeleteAddressCommandHandler {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public void handle(DeleteAddressCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        Address addressToDelete = user.getAddresses().stream()
                .filter(addr -> addr.getId().equals(command.getAddressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(AddressConstant.ADDRESS_NOT_FOUND_OR_NOT_BELONG));

        user.getAddresses().remove(addressToDelete);
        userRepository.save(user);
        addressRepository.deleteById(command.getAddressId());
    }
}
