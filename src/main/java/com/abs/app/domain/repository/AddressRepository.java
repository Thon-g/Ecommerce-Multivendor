package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Address;

import java.util.Optional;
import java.util.List;

public interface AddressRepository {
    Address save(Address address);
    Optional<Address> findById(Long id);
    void deleteById(Long id);
}
