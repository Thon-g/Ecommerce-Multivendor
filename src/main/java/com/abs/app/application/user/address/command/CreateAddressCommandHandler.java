package com.abs.app.application.user.address.command;

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
public class CreateAddressCommandHandler {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public void handle(CreateAddressCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        Address address = new Address();
        address.setName(command.getRequestDto().getName());
        address.setLocality(command.getRequestDto().getLocality());
        address.setAddress(command.getRequestDto().getAddress());
        address.setCity(command.getRequestDto().getCity());
        address.setState(command.getRequestDto().getState());
        address.setPinCode(command.getRequestDto().getPinCode());
        address.setPhone(command.getRequestDto().getPhone());

        Address savedAddress = addressRepository.save(address);
        user.getAddresses().add(savedAddress);
        userRepository.save(user);
    }
}
