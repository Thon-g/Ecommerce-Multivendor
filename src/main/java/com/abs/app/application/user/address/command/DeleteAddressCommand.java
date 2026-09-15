package com.abs.app.application.user.address.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAddressCommand {
    private String userId;
    private Long addressId;
}
