package com.abs.app.infrastructure.persistence;

import com.abs.app.domain.entity.Address;
import com.abs.app.domain.repository.AddressRepository;
import com.abs.app.infrastructure.persistence.jpa.AddressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressRepository {
    private final AddressJpaRepository addressJpaRepository;

    @Override
    public Address save(Address address) {
        return addressJpaRepository.save(address);
    }

    @Override
    public Optional<Address> findById(Long id) {
        return addressJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        addressJpaRepository.deleteById(id);
    }
}
